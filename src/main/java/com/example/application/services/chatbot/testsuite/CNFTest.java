package com.example.application.services.chatbot.testsuite;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CNFTest {

    public static void main(String[] args) throws InterruptedException {
        ChatBot.init();
//        for(Map.Entry<String, List<String>> entry: cnf.getCnf().entrySet()){
//            System.out.println(entry.getKey());
//            for(String symbol: entry.getValue()) System.out.print(symbol + " | ");
//            System.out.println();
//        }
//        System.out.println(cnf.yields("Monday")[0]);
        String query = "Which lectures are there on Monday at 9 ";
        System.out.println(CYK.isValidLanguage(query));
        System.out.println(ChatBot.respondTo(query));
    }
}
