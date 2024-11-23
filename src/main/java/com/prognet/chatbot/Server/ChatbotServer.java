package com.prognet.chatbot.Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatbotServer {
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1933);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
                System.out.println("Client count: " + ClientHandler.getClientCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
