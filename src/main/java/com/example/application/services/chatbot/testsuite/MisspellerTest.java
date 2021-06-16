package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.BaseClassifier;
import com.example.application.services.chatbot.spellcheckML.Misspeller;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MisspellerTest {
    @Test
    void fill(){
        CFG.loadRules();
        BaseClassifier.init();
        Misspeller.fill(BaseClassifier.getAllWords(),5,1);
        for(List<String> misspellings : Misspeller.getMisspellings().values()){
            System.out.println("-------------");
            for(String misspelling : misspellings){
                System.out.print(misspelling + " ");
            }
            System.out.println("");
        }
    }
}
