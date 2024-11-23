package com.prognet.chatbot.Database.models;

import com.prognet.chatbot.Database.Core.DatabaseManager;

public class Users {
    private DatabaseManager dbManager;
    private String username;
    private String password;

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean login() {
        String query = "SELECT * FROM users WHERE username = '" + this.username + "' AND password = '" + this.password + "'";
        return dbManager.executeQuery(query) != null;
    }

    public boolean register() {
        String query = "INSERT INTO users (username, password) VALUES ('" + this.username + "', '" + this.password + "')";
        return dbManager.executeUpdate(query) > 0;
    }

    public String getId() {
        String query = "SELECT id FROM users WHERE username = '" + this.username + "'";
        return dbManager.executeQuery(query).toString();
    }
    
}
