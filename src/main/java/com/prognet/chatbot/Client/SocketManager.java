package com.prognet.chatbot.Client;

import java.io.*;
import java.net.*;

import com.prognet.chatbot.Client.Config.Config;

public class SocketManager {
    private static SocketManager instance;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;

    private SocketManager() {
        try {
            socket = new Socket(Config.HOST, Config.PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void send(String message) {
        writer.println(message);
        writer.flush();
    }

    public String receive() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}