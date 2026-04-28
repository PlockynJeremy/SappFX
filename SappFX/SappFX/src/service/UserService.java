package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.LoginResponse;
import model.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UserService {

    private static final String LOGIN_URL = "http://localhost:3000/api/users/login";
    private static final String USERS_URL = "http://localhost:3000/api/users";

    private final Gson gson = new Gson();

    public LoginResponse login(String username, String password) {
        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(new Credentials(username, password));
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                return gson.fromJson(reader, LoginResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() throws IOException {
        try {
            URL url = new URL(USERS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                Type listType = TypeToken.getParameterized(List.class, User.class).getType();
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

    public void addUser(String username, String password, String role) throws IOException {
        try {
            URL url = new URL(USERS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(new NewUser(username, password, role));
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
                throw new IOException("HTTP " + conn.getResponseCode());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void deleteUser(int id) throws IOException {
        try {
            URL url = new URL(USERS_URL + "/" + id);
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

    static class Credentials {
        String username;
        String password;
        Credentials(String u, String p) { this.username = u; this.password = p; }
    }

    static class NewUser {
        String username;
        String password;
        String role;
        NewUser(String u, String p, String r) { this.username = u; this.password = p; this.role = r; }
    }
}
