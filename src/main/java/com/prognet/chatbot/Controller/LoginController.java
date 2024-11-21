package com.prognet.chatbot.Controller;

import com.prognet.chatbot.View.Login;
public class LoginController {
    private Login login;

    public LoginController() {
        this.login = new Login(this);
        login.setVisible(true);
    }
}
