package com.prognet.chatbot.Server;

import java.util.Map;
import com.prognet.chatbot.Database.models.Users;

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
            if (user.login()) {
                response = "{\"status\": \"success\", \"message\": \"User logged in successfully.\"}";
            } else {
                response = "{\"status\": \"error\", \"message\": \"User login failed.\"}";
            }
        } else if ("predict".equals(action)) {
            Prediction prediction = new Prediction();
            String predictionResponse = prediction.getPrediction(request.get("message"));
            response = "{\"status\": \"success\", \"message\": \"Prediction successful.\", \"prediction\": \"" + predictionResponse + "\"}";
        } else if ("get_id".equals(action)) {
            Users user = new Users(request.get("username"), "");
            String id = user.getId();
            response = "{\"status\": \"success\", \"message\": \"ID retrieved successfully.\", \"id\": \"" + id + "\"}";
        } else {
            response = "{\"status\": \"error\", \"message\": \"Unknown action.\"}";
        }

        return response;
    }
}