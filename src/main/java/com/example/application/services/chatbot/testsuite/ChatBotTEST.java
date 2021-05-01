package com.example.application.services.chatbot.testsuite;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

public class ChatBotTEST {

    @Test
    public void givesAnswer(){
        ChatBot.init();
        assert ChatBot.respondTo("Which lectures are there on Monday at 9").equals("We start the week with math ");
    }

    @Test
    public void notGivesAnswer(){
        ChatBot.init();
        System.out.println(ChatBot.respondTo("Which lectures are there on Wednesday at 9"));
        assert ChatBot.respondTo("Which lectures are there on Wednesday at 9").equals("I don't know");
    }
}
