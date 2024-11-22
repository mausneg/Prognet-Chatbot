package com.prognet.chatbot.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import com.prognet.chatbot.Database.models.User;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Received JSON: " + line);

                Map<String, String> request = parseJson(line);
                String action = request.get("action");

                String response;
                switch (action) {
                    case "register":
                        User user = new User(request.get("username"), request.get("password"));
                        if (user.register()) {
                            response = "{\"status\": \"success\", \"message\": \"User registered successfully.\"}";
                        } else {
                            response = "{\"status\": \"error\", \"message\": \"User registration failed.\"}";
                        }
                        break;
                    case "login":
                        response = "{\"status\": \"success\", \"message\": \"Login successful.\"}";
                        break;
                    case "chat":
                        response = "{\"status\": \"success\", \"message\": \"Chat message received.\"}";
                        break;
                    default:
                        response = "{\"status\": \"error\", \"message\": \"Unknown action.\"}";
                        break;
                }

                out.println(response);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to parse JSON string into key-value pairs
    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.replace("{", "").replace("}", "").replace("\"", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }
}