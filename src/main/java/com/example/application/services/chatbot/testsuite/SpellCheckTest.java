package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.BaseClassifier;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.spellcheck.SpellCheck;
import com.example.application.services.chatbot.spellcheckML.Misspeller;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SpellCheckTest {

    @Test
    void bestMatch(){
        CFG.loadRules();
        BaseClassifier.init();
        double score = 0;
        for (int i = 0; i < 1000; i++) {
            List<String> phrase = CFG.getAllPhrases().get((int) (Math.random() * (double) CFG.getAllPhrases().size()));
            String word = phrase.get((int) (Math.random() * (double) phrase.size()));
            String misspelled = Misspeller.mutate(new StringBuilder(word));
            if(SpellCheck.bestMatch(misspelled).equals(word.toLowerCase())){
                score++;
            }
            System.out.println(i +". word: "+ word + " | misspelled: " + misspelled);
        }
        double percentage = score / 1000d;
        System.out.println("Final score: " + percentage);
    }
}
