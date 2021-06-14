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

        System.out.println(Classifier.process("What lectures are there on Sunday at 9?"));

    }
}
