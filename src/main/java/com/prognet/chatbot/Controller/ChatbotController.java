package com.prognet.chatbot.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.prognet.chatbot.Client.SocketManager;
import com.prognet.chatbot.View.ChatCardBot;
import com.prognet.chatbot.View.ChatCardClient;
import com.prognet.chatbot.View.Chatbot;


public class ChatbotController {
    private Chatbot chatbotClients;
    private int userId;
    private SocketManager socketManager;
    private int historyId;
    public ChatbotController(int userId){
        this.historyId = -1;
        this.socketManager = SocketManager.getInstance();
        this.userId = userId;
        this.chatbotClients = new Chatbot(this);
        this.chatbotClients.setVisible(true);
        displayHistories();
    }

    public void displayChatCardClient(String message, String time){
        ChatCardClient chatCardClient = new ChatCardClient(message, time);
        chatbotClients.addChatCardClient(chatCardClient);
    }

    public void displayChatCardBot(String message, String time){
        ChatCardBot chatCardBot = new ChatCardBot(message, time);
        chatbotClients.addChatCardBot(chatCardBot);
    }

    public void displayHistories(){
        String message = String.format("{\"action\": \"history\", \"userId\": \"%s\"}", this.userId);
        SocketManager.getInstance().send(message);
        String response = socketManager.receive();

    }

    public String getPrediction(String message){
        String response = "";
        String messageJson = String.format("{\"action\": \"predict\", \"message\": \"%s\"}", message);
        socketManager.send(messageJson);
        response = socketManager.receive();
        response = response.split("\"prediction\": \"")[1];
        return response;
    }

    public void setHistoryId(int historyId){
        this.historyId = historyId;
    }

    public void insertChat(String clientMessage, String botMessage, LocalDateTime clientTime, LocalDateTime botTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String clientTimeString = clientTime.format(formatter);
        String botTimeString = botTime.format(formatter);
        String message = String.format("{\"action\": \"chat\", \"history_id\": \"%s\",\"userId\": \"%s\", \"clientMessage\": \"%s\", \"botMessage\": \"%s\", \"clientTime\": \"%s\", \"botTime\": \"%s\"}", this.historyId, this.userId, clientMessage, botMessage, clientTimeString, botTimeString);
        socketManager.send(message);
        String response = socketManager.receive();
        if (historyId == -1)
            this.historyId = Integer.parseInt(response.split("\"historyId\": \"")[1]);

    }


}
