package com.manogstudios.racingsimulator.network;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class SupabaseAuth {

    public static boolean isLoggedIn = false;
    public static String accessToken = null;
    public static String userId = null;

    static final String SUPABASE_URL = "https://trfecuqpkrjobgxrmgwm.supabase.co";
    static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRyZmVjdXFwa3Jqb2JneHJtZ3dtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTIwNzc0MjgsImV4cCI6MjA2NzY1MzQyOH0._sByi5zdyPnViPIzTsidac2REfnq5GCC89_zD7TLyEs";

    public static void login(String email, String password, Consumer<Boolean> callback) {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + "/auth/v1/token?grant_type=password"))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(String.format(
                        "{\"email\":\"%s\",\"password\":\"%s\"}", email, password
                    )))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    isLoggedIn = true;

                    JSONObject json = new JSONObject(response.body());
                    accessToken = json.getString("access_token");

                    // Extract and store user ID
                    String userId = json.getJSONObject("user").getString("id");
                    SupabaseAuth.userId = json.getJSONObject("user").getString("id");

                    callback.accept(true);
                } else {
                    isLoggedIn = false;
                    callback.accept(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                isLoggedIn = false;
                callback.accept(false);
            }
        }).start();
    }


    public static void logout() {
        isLoggedIn = false;
        accessToken = null;
    }


    public static boolean registerUser(String email, String password) {
        try {
            URL url = new URL(SUPABASE_URL + "/auth/v1/signup");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("apikey", API_KEY);
            conn.setDoOutput(true);

            String jsonInput = String.format(
                "{ \"email\": \"%s\", \"password\": \"%s\" }",
                email, password
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();

            InputStream stream = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println("Supabase registration response: " + response.toString());

            conn.disconnect();
            return status == 200 || status == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
