package com.prognet.chatbot.Controller;

import com.prognet.chatbot.View.Register;

public class RegisterController {
    private Register register;

    public RegisterController() {
        this.register = new Register(this);
        register.setVisible(true);
    }
}
