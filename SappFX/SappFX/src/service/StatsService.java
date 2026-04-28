package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.StatArticle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StatsService {

    private static final String BASE_URL   = "http://localhost:3000/api/articles";
    private final Gson gson = new Gson();

    public List<StatArticle> getTopVues() throws IOException {
        return fetchList(BASE_URL + "/top-vues");
    }

    public List<StatArticle> getTopVentes() throws IOException {
        return fetchList(BASE_URL + "/top-ventes");
    }

    private List<StatArticle> fetchList(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            Type listType = TypeToken.getParameterized(List.class, StatArticle.class).getType();
            return gson.fromJson(reader, listType);
        } else {
            throw new IOException("HTTP " + conn.getResponseCode());
        }
    }
}
