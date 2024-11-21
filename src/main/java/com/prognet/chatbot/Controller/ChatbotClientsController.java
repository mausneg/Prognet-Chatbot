package com.prognet.chatbot.Controller;

import com.prognet.chatbot.View.ChatbotClients;

public class ChatbotClientsController {
    private ChatbotClients chatbotClients;
    public ChatbotClientsController(){
        chatbotClients = new ChatbotClients(this);
        chatbotClients.setVisible(true);
    }
}
