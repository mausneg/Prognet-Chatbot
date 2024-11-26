package com.prognet.chatbot.Server;

import java.util.Map;
import java.util.ArrayList;

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
                historyId = history.insertHistory(Integer.parseInt(request.get("user_id")));
            }
            history.updateHistory(historyId);
            Chats chat = new Chats();
            if (chat.insertChat(historyId, request.get("clientMessage"), request.get("botMessage"), request.get("clientTime"), request.get("botTime"))){
                response = "{\"status\": \"success\", \"message\": \"Chat inserted successfully.\", \"history_id\": \"" + historyId + "\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"Chat insertion failed.\", \"history_id\": \"" + historyId + "\"}";
            }
        } else if("history".equals(action)){
            Histories history = new Histories();
            Chats chat = new Chats();
            int userId = Integer.parseInt(request.get("userId"));
            ArrayList<Integer> historyIds = history.getHistoryIds(userId);
            ArrayList<String> firstMessages = chat.getFirstMessages(historyIds);
            response = "{\"status\": \"success\", \"message\": \"Histories retrieved successfully.\", \"history_ids\": \"" + historyIds + "\", \"first_messages\": \"" + firstMessages + "\"}";
        } else if("clicked_chat".equals(action)){
            Chats chat = new Chats();
            int historyId = Integer.parseInt(request.get("history_id"));
            ArrayList<String> clickedChat = chat.getMessages(historyId);
            response = "{\"status\": \"success\", \"message\": \"Chat retrieved successfully.\", \"clicked_chat\": \"" + clickedChat + "\"}";
        } else if("delete_chat".equals(action)){
            Chats chat = new Chats();
            Histories history = new Histories();
            int historyId = Integer.parseInt(request.get("history_id"));
            if (chat.deleteChat(historyId) && history.deleteHistory(historyId)){
                response = "{\"status\": \"success\", \"message\": \"Chat deleted successfully.\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"Chat deletion failed.\"}";
            }
        }
        else {
            response = "{\"status\": \"error\", \"message\": \"Unknown action.\"}";
        }

        return response;
    }
}