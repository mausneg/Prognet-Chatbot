package com.prognet.chatbot.Database.models;

import com.prognet.chatbot.Database.Core.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Chats {
    private DatabaseManager dbManager;
    private int historyId;
    private String clientMessage;
    private String botMessage;
    private LocalDateTime clientTime;
    private LocalDateTime botTime;

    public Chats(int historyId, String clientMessage, String botMessage, String clientTime, String botTime) {
        this.dbManager = DatabaseManager.getInstance();
        this.historyId = historyId;
        this.clientMessage = clientMessage;
        this.botMessage = botMessage;
        this.clientTime = LocalDateTime.parse(clientTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        this.botTime = LocalDateTime.parse(botTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
    }

    public boolean insertChat() {
        String query = "INSERT INTO chats (history_id, client_message, bot_message, client_datetime, bot_datetime) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, this.historyId);
            stmt.setString(2, this.clientMessage);
            stmt.setString(3, this.botMessage);
            stmt.setTimestamp(4, Timestamp.valueOf(this.clientTime));
            stmt.setTimestamp(5, Timestamp.valueOf(this.botTime));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}