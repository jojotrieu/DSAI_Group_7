package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.BaseClassifier;
import org.junit.jupiter.api.Test;

public class BaseClassifierTest {
    @Test
    void process(){
        CFG.loadRules();
        BaseClassifier.init();
        System.out.println(BaseClassifier.process("What lectures are there on Sunday at 9?"));

    }
}
