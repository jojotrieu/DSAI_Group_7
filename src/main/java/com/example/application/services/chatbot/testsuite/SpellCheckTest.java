package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.BaseClassifier;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.spellcheck.SpellCheck;
import org.junit.jupiter.api.Test;

public class SpellCheckTest {

    @Test
    void bestMatch(){
        CFG.loadRules();
        BaseClassifier.init();
        //SpellCheck.bestMatch("waht");
        System.out.println(SpellCheck.bestMatch("waht"));
        System.out.println(SpellCheck.bestMatch("prorgma"));
    }
}
