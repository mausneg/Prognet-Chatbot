package com.prognet.chatbot.Controller;

import com.prognet.chatbot.Database.Core.DatabaseManager;
import com.prognet.chatbot.View.Login;

public class LoginController {
    private String username;
    private String password;
    private Login login;
    private DatabaseManager dbManager;

    public LoginController() {
        this.login = new Login(this);
        this.dbManager = new DatabaseManager();
        login.setVisible(true);
    }
    public boolean login(String username, String password) {
        this.username = username;
        this.password = password;
        return dbManager.login(username, password);
    }
    public int getUsername(String username) {
        return dbManager.getUsername(username);
    }
}
