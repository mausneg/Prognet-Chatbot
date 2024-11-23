package com.prognet.chatbot.Controller;

import com.prognet.chatbot.Client.SocketManager;
import com.prognet.chatbot.Client.Config.Config;
import com.prognet.chatbot.View.Register;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterController {
    private Register register;
    private SocketManager socketManager;

    public RegisterController() {
        this.register = new Register(this);
        this.register.setVisible(true);
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

    public void register(String username, String password) {
        String encryptedPassword = encryptPassword(password);
        String message = String.format("{\"action\": \"register\", \"username\": \"%s\", \"password\": \"%s\"}", username, encryptedPassword);
        socketManager.send(message);

        String response = socketManager.receive();
        System.out.println("Response from server: " + response);

        if (response.contains("\"status\": \"success\"")) {
            this.register.showMessage("User registered successfully");
            new LoginController();
            this.register.dispose();
        } else {
            this.register.showMessage("User registration failed");
        }
    }

    public void close() {
        socketManager.close();
    }
}