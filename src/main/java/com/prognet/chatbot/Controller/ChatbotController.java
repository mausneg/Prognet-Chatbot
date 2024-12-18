package com.prognet.chatbot.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.prognet.chatbot.Client.SocketManager;
import com.prognet.chatbot.View.ChatCardBot;
import com.prognet.chatbot.View.ChatCardClient;
import com.prognet.chatbot.View.Chatbot;
import com.prognet.chatbot.View.HistoryCard;


public class ChatbotController {
    private Chatbot chatbotClients;
    private int userId;
    private String username;
    private SocketManager socketManager;
    private int historyId;
    private HistoryCard clickedHistoryCard;
    private ArrayList<HistoryCard> historyCards;

    public ChatbotController(int userId, String username){
        this.historyId = -1;
        this.socketManager = SocketManager.getInstance();
        this.userId = userId;
        this.username = username;
        this.chatbotClients = new Chatbot(this);
        this.chatbotClients.setVisible(true);
        this.historyCards = new ArrayList<>();
        this.getHistories();
        this.setUsername();
    }

    public void setUsername(){
        this.chatbotClients.setUsername(username);
    }

    public void newChat(){
        chatbotClients.clearChatCards();
        this.historyId = -1;
        this.clickedHistoryCard.setBackground(clickedHistoryCard.getDefaultBackgroundColor());
        this.clickedHistoryCard.setClicked(false);
        this.clickedHistoryCard = null;
    }
    public void deleteChat(int historyId) {
        String message = String.format("{\"action\": \"delete_chat\", \"history_id\": \"%s\"}", historyId);
        socketManager.send(message);
        String response = socketManager.receive();

        if (response.contains("success")) {
            HistoryCard cardToRemove = null;
            for (HistoryCard historyCard : historyCards) {
                if (historyCard.getHistoryId() == historyId) {
                    cardToRemove = historyCard;
                    break;
                }
            }
            if (cardToRemove != null) {
                historyCards.remove(cardToRemove);
                if (clickedHistoryCard != null && clickedHistoryCard.getHistoryId() == historyId) {
                    chatbotClients.clearChatCards();
                    clickedHistoryCard = null;
                    this.historyId = -1;
                }
                displayHistories();
            }
        }
    }
    public void setChatClicked() {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
        chatbotClients.clearChatCards();
        String message = String.format("{\"action\": \"clicked_chat\", \"history_id\": \"%s\"}", this.historyId);
        socketManager.send(message);
        String response = socketManager.receive();
    
        String clickedChat = response.split("\"clicked_chat\": \"")[1].split("\"}")[0];
        clickedChat = clickedChat.replace("[", "").replace("]", "");
        String[] clickedChatArray = clickedChat.split(", ");
    
        for (int i = 0; i < clickedChatArray.length; i += 4) {
            String clientMessage = clickedChatArray[i];
            String clientTime = clickedChatArray[i + 1];
            String botMessage = clickedChatArray[i + 2];
            String botTime = clickedChatArray[i + 3];
    
            LocalDateTime clientDateTime = LocalDateTime.parse(clientTime, inputFormatter);
            LocalDateTime botDateTime = LocalDateTime.parse(botTime, inputFormatter);
            String formattedClientTime = clientDateTime.format(outputFormatter);
            String formattedBotTime = botDateTime.format(outputFormatter);
    
            displayChatCardClient(clientMessage, formattedClientTime);
            displayChatCardBot(botMessage, formattedBotTime);
        }
    }
    public void setClickedHistoryCard(HistoryCard historyCard){
        if (clickedHistoryCard != null) {
            clickedHistoryCard.setBackground(clickedHistoryCard.getDefaultBackgroundColor());
            clickedHistoryCard.setClicked(false);
        }
        clickedHistoryCard = historyCard;
        clickedHistoryCard.setBackground(clickedHistoryCard.getHoverBackgroundColor());
        clickedHistoryCard.setClicked(true);
        this.historyId = clickedHistoryCard.getHistoryId();
    }

    public void editHistoryCard() {
        HistoryCard targetCard = null;
        for (HistoryCard historyCard : historyCards) {
            if (historyCard.getHistoryId() == this.historyId) {
                targetCard = historyCard;
                break;
            }
        }
        if (targetCard != null) {
            historyCards.remove(targetCard);
            historyCards.add(0, targetCard);
        }
    }
    
    public void getHistories() {
        String message = String.format("{\"action\": \"history\", \"userId\": \"%s\"}", this.userId);
        socketManager.send(message);
        String response = socketManager.receive();
        
        String historyIdsString = response.split("\"history_ids\": \"")[1].split("\", \"first_messages\": \"")[0];
        historyIdsString = historyIdsString.replace("[", "").replace("]", "");
        String firstMessagesString = response.split("\", \"first_messages\": \"")[1].split("\"}")[0];
        firstMessagesString = firstMessagesString.replace("[", "").replace("]", "");
        
        if (historyIdsString.equals("")) {
            return;
        }

        String[] historyIds = historyIdsString.split(", ");
        String[] firstMessages = firstMessagesString.split(", ");

        for (int i = 0; i < historyIds.length; i++) {
            int historyId = Integer.parseInt(historyIds[i]);
            String firstMessage = firstMessages[i].replaceAll("^\"|\"$", ""); // Remove surrounding quotes
            HistoryCard historyCard = new HistoryCard(historyId, firstMessage, this);
            historyCards.add(historyCard);
        }
        displayHistories();
    }

    public void displayHistories(){
        chatbotClients.clearHistoryCards();
        for (HistoryCard historyCard : historyCards) {
            chatbotClients.addHistoryCard(historyCard);
        }
    }    
    
    public void displayChatCardClient(String message, String time){
        ChatCardClient chatCardClient = new ChatCardClient(message, time);
        chatbotClients.addChatCardClient(chatCardClient);
    }

    public void displayChatCardBot(String message, String time){
        ChatCardBot chatCardBot = new ChatCardBot(message, time);
        chatbotClients.addChatCardBot(chatCardBot);
    }
    public String getPrediction(String message) {
        String response = "";
        String messageJson = String.format("{\"action\": \"predict\", \"message\": \"%s\"}", message);
        socketManager.send(messageJson);
        response = socketManager.receive();
        response = response.split("\"prediction\": \"")[1].split("\"")[0];
        return response;
    }

    public void insertChat(String clientMessage, String botMessage, LocalDateTime clientTime, LocalDateTime botTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        String message = String.format("{\"action\": \"chat\", \"user_id\": \"%s\", \"history_id\": \"%s\", \"clientMessage\": \"%s\", \"botMessage\": \"%s\", \"clientTime\": \"%s\", \"botTime\": \"%s\"}",
                this.userId, this.historyId, clientMessage, botMessage, clientTime.format(formatter), botTime.format(formatter));
        socketManager.send(message);
        String response = socketManager.receive();
        if (historyId == -1){
            this.historyId = Integer.parseInt(response.split("\"history_id\": \"")[1].split("\"")[0]);
            HistoryCard historyCard = new HistoryCard(this.historyId, clientMessage, this);
            historyCards.add(0, historyCard);
            setClickedHistoryCard(historyCard);
            displayHistories();
        }
        else{
            if (this.historyCards.get(0).getHistoryId() != this.historyId) {
                editHistoryCard();
                displayHistories();
        }
        }
    }
}
