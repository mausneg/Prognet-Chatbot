package com.prognet.chatbot.Controller;

import com.prognet.chatbot.Database.Core.DatabaseManager;
import com.prognet.chatbot.View.Register;

public class RegisterController {
    private String username;
    private String password;
    private Register register;
    private DatabaseManager dbManager;

    public RegisterController() {
        this.register = new Register(this);
        this.dbManager = new DatabaseManager();
        register.setVisible(true);
    }

    public boolean register(String username, String password) {
        this.username = username;
        this.password = password;
        return dbManager.register(username, password);
    }
}
