package com.prognet.chatbot.Server;

import java.util.Map;
import com.prognet.chatbot.Database.models.Users;
import com.prognet.chatbot.Database.models.Histories;
import com.prognet.chatbot.Database.models.Chats;

public class RequestDispatcher {

    public String dispatch(Map<String, String> request) {
        String response;
        String action = request.get("action");

        if ("register".equals(action)) {
            Users user = new Users(request.get("username"), request.get("password"));
            if (user.register()) {
                response = "{\"status\": \"success\", \"message\": \"User registered successfully.\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"User registration failed.\"}";
            }
        } else if ("login".equals(action)) {
            Users user = new Users(request.get("username"), request.get("password"));
            int userId = user.login();
            if (userId != -1) {
                response = "{\"status\": \"success\", \"message\": \"Login successful.\", \"user_id\": \"" + userId + "\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"Login failed.\"}";
            }
        } else if ("predict".equals(action)) {
            Prediction prediction = new Prediction();
            String predictionResponse = prediction.getPrediction(request.get("message"));
            response = "{\"status\": \"success\", \"message\": \"Prediction successful.\", \"prediction\": \"" + predictionResponse + "\"}";
        } else if("chat".equals(action)){
            Histories history = new Histories();
            int historyId = Integer.parseInt(request.get("history_id"));
            if (historyId == -1){
                historyId = history.insertHistory(Integer.parseInt(request.get("userId")));
            }
            history.updateHistory(historyId);
            Chats chat = new Chats(historyId, request.get("clientMessage"), request.get("botMessage"), request.get("clientTime"), request.get("botTime"));
            if (chat.insertChat()){
                response = "{\"status\": \"success\", \"message\": \"Chat inserted successfully.\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"Chat insertion failed.\"}";
            }

        }
        
        else {
            response = "{\"status\": \"error\", \"message\": \"Unknown action.\"}";
        }

        return response;
    }
}