package com.prognet.chatbot.Controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.prognet.chatbot.Client.SocketManager;
import com.prognet.chatbot.Database.Core.DatabaseManager;
import com.prognet.chatbot.View.Login;

public class LoginController {
    private Login login;
    private SocketManager socketManager;

    public LoginController() {
        this.login = new Login(this);
        this.login.setVisible(true);
        this.socketManager = SocketManager.getInstance();
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void login(String username, String password) {
        String encryptedPassword = encryptPassword(password);
        String message = String.format("{\"action\": \"login\", \"username\": \"%s\", \"password\": \"%s\"}", username, encryptedPassword);
        socketManager.send(message);

        String response = socketManager.receive();
        System.out.println("Response from server: " + response);

        if (response.contains("\"status\": \"success\"")) {
            this.login.showMessage("Login successful");
            String messageJson = String.format("{\"action\": \"get_id\", \"username\": \"%s\"}", username);
            socketManager.send(messageJson);
            String idResponse = socketManager.receive();
            String id = idResponse.split("\"id\": \"")[1].split("\"}")[0];
            new ChatbotController(id);
            this.login.dispose();
        } else {
            this.login.showMessage("Login failed");
        }
    }

    public void close() {
        socketManager.close();
    }

}
