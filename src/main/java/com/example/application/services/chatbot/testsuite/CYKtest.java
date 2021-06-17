package com.example.application.services.chatbot.testsuite;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.CYK;
import org.junit.jupiter.api.Test;



public class CYKtest {
    @Test
    public void basicTest(){
        CFG.loadRules();
        CNF.initialize();
        assert CYK.isValidLanguage("which lectures are there on monday at 9");
        assert CYK.isValidLanguage("how do i get to spacebox");
        assert CYK.isValidLanguage("WHO IS PIETRO");


    }

}

