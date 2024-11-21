package com.prognet.chatbot.Controller;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Prediction {
    public static void main(String[] args) {
        try {
            String url = "http://localhost:5000/predict";
            String inputText = "PEMROGRAMAN INTERNET ITU APA?";
            String payload = "{\"text\": \"" + inputText + "\"}";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Status Code: " + responseCode);

            Scanner scanner;
            if (responseCode == 200) {
                scanner = new Scanner(con.getInputStream());
            } else {
                scanner = new Scanner(con.getErrorStream());
            }
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            String responseValue = extractResponseValue(response);
            System.out.println("Response: " + responseValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractResponseValue(String jsonResponse) {
        String key = "\"response\":";
        int startIndex = jsonResponse.indexOf(key) + key.length();
        int endIndex = jsonResponse.indexOf("}", startIndex);
        return jsonResponse.substring(startIndex, endIndex).trim().replaceAll("^\"|\"$", "");
    }
}