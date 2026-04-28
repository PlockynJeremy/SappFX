package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Article;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ArticleService {

    private static final String BASE_URL   = "http://localhost:3000/api/articles";
    private static final String BOUNDARY   = "----SappFXBoundary" + System.currentTimeMillis();
    private static final String LINE_FEED  = "\r\n";

    private final Gson gson = new Gson();

    public List<Article> findAll() throws IOException {
        try {
            URL url = new URL(BASE_URL + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                Type listType = TypeToken.getParameterized(List.class, Article.class).getType();
                return gson.fromJson(reader, listType);
            } else {
                throw new IOException("HTTP " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void create(Article article, File imageFile) throws IOException {
        sendMultipart(BASE_URL + "/", "POST", article, imageFile);
    }

    public void update(int id, Article article, File imageFile) throws IOException {
        sendMultipart(BASE_URL + "/" + id, "PUT", article, imageFile);
    }

    public void delete(int id) throws IOException {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
                throw new IOException("HTTP " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void sendMultipart(String urlStr, String method, Article article, File imageFile) throws IOException {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            try (OutputStream os = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

                addTextField(writer, "Nom",         article.getNom());
                addTextField(writer, "Type",        article.getType());
                addTextField(writer, "couleur",     article.getCouleur()     != null ? article.getCouleur()     : "");
                addTextField(writer, "Prix",        String.valueOf(article.getPrix()));
                addTextField(writer, "Stock",       String.valueOf(article.getStock()));
                addTextField(writer, "Taille",      article.getTaille()      != null ? article.getTaille()      : "");
                addTextField(writer, "Genre",       article.getGenre()       != null ? article.getGenre()       : "");
                addTextField(writer, "Description", article.getDescription() != null ? article.getDescription() : "");

                if (imageFile != null && imageFile.exists()) {
                    String mimeType = Files.probeContentType(imageFile.toPath());
                    if (mimeType == null) mimeType = "image/jpeg";

                    writer.append("--").append(BOUNDARY).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"Image\"; filename=\"")
                          .append(imageFile.getName()).append("\"").append(LINE_FEED);
                    writer.append("Content-Type: ").append(mimeType).append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    Files.copy(imageFile.toPath(), os);
                    os.flush();

                    writer.append(LINE_FEED);
                    writer.flush();
                }

                writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
                writer.flush();
            }

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {

                InputStream errStream = conn.getErrorStream();
                String errMsg = errStream != null ? new String(errStream.readAllBytes()) : "Erreur inconnue";
                throw new IOException("HTTP " + code + " : " + errMsg);
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private void addTextField(PrintWriter writer, String name, String value) {
        writer.append("--").append(BOUNDARY).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
}
