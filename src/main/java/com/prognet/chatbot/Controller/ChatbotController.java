package com.prognet.chatbot.Controller;

import com.prognet.chatbot.View.ChatCardBot;
import com.prognet.chatbot.View.ChatCardClient;
import com.prognet.chatbot.View.Chatbot;


public class ChatbotController {
    private Chatbot chatbotClients;
    private Prediction prediction;
    public ChatbotController(){
        chatbotClients = new Chatbot(this);
        chatbotClients.setVisible(true);
        prediction = new Prediction();
        displayData();
    }

    public String getPrediction(String text){
        return prediction.getPrediction(text);
    }
    
    public void displayChatCardClient(String message, String time){
        ChatCardClient chatCardClient = new ChatCardClient(message, time);
        chatbotClients.addChatCardClient(chatCardClient);
    }

    public void displayChatCardBot(String message, String time){
        ChatCardBot chatCardBot = new ChatCardBot(message, time);
        chatbotClients.addChatCardBot(chatCardBot);
    }

    public void displayData(){
        String [] messagesClient = {"Hello", "Hi", "How are you?", "I'm good"};
        String [] timesClient = {"10:00", "10:05", "10:10", "10:15"};
        String [] messagesBot = {"Hello", "Hi", "I'm good", "What's up?"};
        String [] timesBot = {"10:00", "10:05", "10:10", "10:15"};

        for (int i = 0; i < messagesClient.length; i++){
            displayChatCardClient(messagesClient[i], timesClient[i]);
            displayChatCardBot(messagesBot[i], timesBot[i]);
        }

    }


}
