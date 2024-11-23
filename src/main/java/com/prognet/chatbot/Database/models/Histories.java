package com.prognet.chatbot.Database.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import com.prognet.chatbot.Database.Core.DatabaseManager;

public class Histories {
    private DatabaseManager dbManager;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;
    private int userId;
    private String message;

    public Histories(int userId, String message) {
        this.dbManager = DatabaseManager.getInstance();
        this.createdOn = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.userId = userId;
        this.message = message;
    }

}