package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.Classifier;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifierTest {
    @Test
    void process(){
        CFG.loadRules();
        Classifier.init();
        HashMap<String, List<String>> synonyms = Classifier.getSynonymsMap();
        String s = "yo-dude: like, ... []{}this is a string";
        s = s.replaceAll("[^a-zA-Z0-9]", "");
        System.out.println(s);
        for(Map.Entry<String, List<String>> entry : synonyms.entrySet()){
            System.out.println("----------");
            System.out.println(entry.getKey());
            System.out.println("----------");
            for(String str : entry.getValue()){
                System.out.println(str);
            }
        }
        System.out.println(Classifier.process("What lectures are there on Sunday at 9?"));

    }
}
