package com.prognet.chatbot.Controller;

import com.prognet.chatbot.View.ChatbotClients;
import com.prognet.chatbot.View.ChatCard;

public class ChatbotClientsController {
    private ChatbotClients chatbotClients;
    private ChatCard chatCard;
    public ChatbotClientsController(){
        chatbotClients = new ChatbotClients(this);
        chatbotClients.setVisible(true);
        displayData();
    }

    private void displayDataCard(String messageClient, String timeClient, String messageBot, String timeBot){
        this.chatCard = new ChatCard(messageClient, timeClient, messageBot, timeBot); 
        this.chatbotClients.addChatCard(chatCard);  
    }

    public void displayData(){
        String [] messagesClient = {"Hello", "Hi", "How are you?", "I'm good"};
        String [] timesClient = {"10:00", "10:05", "10:10", "10:15"};
        String [] messagesBot = {"Hello", "Hi", "I'm good", "What's up?"};
        String [] timesBot = {"10:00", "10:05", "10:10", "10:15"};

        for (int i = 0; i < messagesClient.length; i++){
            displayDataCard(messagesClient[i], timesClient[i], messagesBot[i], timesBot[i]);
        }

    }

}
