package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CNFTest {

    public static void main(String[] args){
        CNF cnf = new CNF();
        cnf.initialize();
        for(Map.Entry<String, List<String>> entry: cnf.getCnf().entrySet()){
            System.out.println(entry.getKey());
            for(String symbol: entry.getValue()) System.out.print(symbol + " | ");
            System.out.println();
        }

//        cnf.replace(cnf.getCnf().get("<ACTION>"), 0);
//        System.out.println(cnf.getCnf().get("<ACTION>"));

//        String test = " <LOCATION>", test2 = " <LOCATION> ";
//
//        System.out.println(cnf.unary(test));
//        System.out.println(cnf.unary(test2));
    }
}
