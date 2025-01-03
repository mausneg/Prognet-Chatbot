package com.prognet.chatbot.Database.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import com.prognet.chatbot.Database.Core.DatabaseManager;

public class Histories {
    private DatabaseManager dbManager;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;
    private int userId;

    public Histories() {
        this.dbManager = DatabaseManager.getInstance();
        this.createdOn = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public int insertHistory(int userId) {
        this.userId = userId;
        String query = "INSERT INTO histories (created_on, last_updated, user_id) VALUES (?, ?, ?)";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(this.createdOn));
            stmt.setTimestamp(2, Timestamp.valueOf(this.lastUpdated));
            stmt.setInt(3, userId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void updateHistory(int historyId) {
        this.lastUpdated = LocalDateTime.now();
        String query = "UPDATE histories SET last_updated = ? WHERE history_id = ?";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(this.lastUpdated));
            stmt.setInt(2, historyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getHistoryIds(int userId) {
        ArrayList<Integer> historyIds = new ArrayList<>();
        String query = "SELECT history_id FROM histories WHERE user_id = ? ORDER BY last_updated DESC";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historyIds.add(rs.getInt("history_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyIds;
    }

    public boolean deleteHistory(int historyId) {
        String query = "DELETE FROM histories WHERE history_id = ?";
        try (Connection connection = dbManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}