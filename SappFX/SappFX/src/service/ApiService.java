package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import util.SessionUser;

public class ApiService<T> {

    private final String baseUrl;

    private final Class<T> clazz;

    private final HttpClient client = HttpClient.newHttpClient();

    private final Gson gson;

    public ApiService(String baseUrl, Class<T> clazz) {
        this.baseUrl = baseUrl;
        this.clazz = clazz;

        this.gson = new GsonBuilder().create();
    }

    private String getAuthHeader() {
        String token = SessionUser.getToken();
        if (token != null && !token.isEmpty()) {
            return "Bearer " + token;
        }
        return "";
    }

    public List<T> findAll() throws IOException, InterruptedException {

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/"))
                .GET();

        String authHeader = getAuthHeader();
        if (!authHeader.isEmpty()) {
            reqBuilder.header("Authorization", authHeader);
        }

        HttpRequest req = reqBuilder.build();

        HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {

            JsonReader reader = new JsonReader(new StringReader(resp.body()));
            reader.setLenient(true);
            JsonElement root = JsonParser.parseReader(reader);

            JsonArray array;

            if (root.isJsonArray()) {

                array = root.getAsJsonArray();
            } else {

                String key = clazz.getSimpleName().toLowerCase() + "s";
                array = root.getAsJsonObject().getAsJsonArray(key);
            }

            Type listType = TypeToken
                    .getParameterized(List.class, clazz)
                    .getType();

            return gson.fromJson(array, listType);

        } else {
            throw new IOException("HTTP " + resp.statusCode() + " : " + resp.body());
        }
    }

    public T create(T entity) throws IOException, InterruptedException {

        String json = gson.toJson(entity);

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));

        String authHeader = getAuthHeader();
        if (!authHeader.isEmpty()) {
            reqBuilder.header("Authorization", authHeader);
        }

        HttpResponse<String> resp = client.send(
                reqBuilder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return gson.fromJson(resp.body(), clazz);
        } else {
            throw new IOException("HTTP " + resp.statusCode() + " : " + resp.body());
        }
    }

    public T update(int id, T entity) throws IOException, InterruptedException {

        String json = gson.toJson(entity);

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));

        String authHeader = getAuthHeader();
        if (!authHeader.isEmpty()) {
            reqBuilder.header("Authorization", authHeader);
        }

        HttpResponse<String> resp = client.send(
                reqBuilder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return gson.fromJson(resp.body(), clazz);
        } else {
            throw new IOException("HTTP " + resp.statusCode() + " : " + resp.body());
        }
    }

    public void delete(int id) throws IOException, InterruptedException {

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + id))
                .DELETE();

        String authHeader = getAuthHeader();
        if (!authHeader.isEmpty()) {
            reqBuilder.header("Authorization", authHeader);
        }

        HttpResponse<String> resp = client.send(
                reqBuilder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("HTTP " + resp.statusCode() + " : " + resp.body());
        }
    }
}
