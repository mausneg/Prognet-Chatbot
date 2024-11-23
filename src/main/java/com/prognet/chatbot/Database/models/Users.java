package com.prognet.chatbot.Database.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public int login() {
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, this.username);
            stmt.setString(2, this.password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                return userId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean register() {
        String query = "INSERT INTO users (username, password) VALUES ('" + this.username + "', '" + this.password + "')";
        return dbManager.executeUpdate(query) > 0;
    }

    
}
