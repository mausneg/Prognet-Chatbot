package com.prognet.chatbot.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;


import com.prognet.chatbot.Database.models.Users;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private static int clientCount = 0;
    private RequestDispatcher requestDispatcher;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        requestDispatcher = new RequestDispatcher();
        clientCount++;
        System.out.println("[" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "]");
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        System.out.println("Client count: " + clientCount);
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "]");
                System.out.println("Received JSON: " + line);

                Map<String, String> request = parseJson(line);
                String response = requestDispatcher.dispatch(request);
                
                System.out.println("[" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "]");
                System.out.println("Sending JSON: " + response);
                out.println(response);
                out.flush();
            }
            clientCount--;

            System.out.println("[" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "]");
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            System.out.println("Client count: " + clientCount);
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

    public static int getClientCount() {
        return clientCount;
    }
}