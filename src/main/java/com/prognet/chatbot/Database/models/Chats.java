package com.prognet.chatbot.Database.models;

import com.prognet.chatbot.Database.Core.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Chats {
    private DatabaseManager dbManager;
    private int historyId;
    private String clientMessage;
    private String botMessage;
    private LocalDateTime clientTime;
    private LocalDateTime botTime;

    public Chats(){
        this.dbManager = DatabaseManager.getInstance();
    }


    public boolean insertChat(int historyId, String clientMessage, String botMessage, String clientTime, String botTime) {
        this.historyId = historyId;
        this.clientMessage = clientMessage;
        this.botMessage = botMessage;
        this.clientTime = LocalDateTime.parse(clientTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        this.botTime = LocalDateTime.parse(botTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));

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

    public ArrayList<String> getFirstMessages(ArrayList<Integer> historyIds) {
        ArrayList<String> firstMessages = new ArrayList<>();
        String query = "SELECT client_message FROM chats WHERE history_id = ? ORDER BY client_datetime ASC LIMIT 1";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int historyId : historyIds) {
                stmt.setInt(1, historyId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstMessages.add(rs.getString("client_message"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return firstMessages;
    }

    public ArrayList<String> getMessages(int historyId) {
        ArrayList<String> messages = new ArrayList<>();
        String query = "SELECT client_message, bot_message, client_datetime, bot_datetime FROM chats WHERE history_id = ? ORDER BY client_datetime ASC";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(rs.getString("client_message"));
                messages.add(rs.getTimestamp("client_datetime").toString());
                messages.add(rs.getString("bot_message"));
                messages.add(rs.getTimestamp("bot_datetime").toString());

            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return messages;
    }
}