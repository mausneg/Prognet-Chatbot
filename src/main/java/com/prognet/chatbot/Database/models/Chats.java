package com.prognet.chatbot.Database.models;

import com.prognet.chatbot.Database.Core.DatabaseManager;

public class Chats {
    private DatabaseManager dbManager;
    private int historyId;
    private String clientMessage;
    private String botMessage;
    private String clientTime;
    private String botTime;

    public Chats(int historyId, String clientMessage, String botMessage, String clientTime, String botTime) {
        this.historyId = historyId;
        this.clientMessage = clientMessage;
        this.botMessage = botMessage;
        this.clientTime = clientTime;
        this.botTime = botTime;
    }

    public boolean insertChat() {
        String query = "INSERT INTO chats (history_id, client_message, bot_message, client_dataetime, bot_datetime) VALUES ('" + this.historyId + "', '" + this.clientMessage + "', '" + this.botMessage + "', '" + this.clientTime + "', '" + this.botTime + "')";
        return dbManager.executeUpdate(query) > 0;
    }
    
}
