package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.spellcheckML.Rephraser;
import org.junit.jupiter.api.Test;

public class RephraserTest {
    @Test
    void fill(){
        CFG.loadRules();
        Rephraser.fill(5);
        for(String phrase : Rephraser.getRephrased()){
            System.out.println(phrase);
        }
        System.out.println("Total set size: " + Rephraser.getRephrased().size());
    }
}
