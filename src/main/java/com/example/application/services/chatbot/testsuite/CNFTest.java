package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CNFTest {

    public static void main(String[] args){
        CNF cnf = new CNF();
        cnf.initialize();
//        for(Map.Entry<String, List<String>> entry: cnf.getCnf().entrySet()){
//            System.out.println(entry.getKey());
//            for(String symbol: entry.getValue()) System.out.print(symbol + " | ");
//            System.out.println();
//        }
//        System.out.println(cnf.yields("Monday")[0]);
        String query = "Which lectures are there on Monday at 9 ";
        System.out.println(cnf.CYK(query));
        System.out.println(cnf.getAnswer(query));
    }
}