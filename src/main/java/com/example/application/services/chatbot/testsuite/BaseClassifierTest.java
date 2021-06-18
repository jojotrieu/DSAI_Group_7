package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.*;
import com.example.application.services.utils.TextFileIO;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BaseClassifierTest {
    @Test
    void process(){
        CFG.loadRules();
        Skills.loadActions();
        CNF.initialize();
        BaseClassifier.init();
        List<String> phrases =
                TextFileIO.read("src/main/java/com/example/application/services/chatbot/DataBenchMark/invalidphrases.txt");
        List<String> labels =
                TextFileIO.read("src/main/java/com/example/application/services/chatbot/DataBenchMark/invalidlabels.txt");
        double correct = 0;
        for (int i = 0; i < phrases.size(); i++) {
            String phrase = phrases.get(i);
            String label = labels.get(i);
            System.out.println(i + ". " + label + " | " + phrase);
            String prediction = BaseClassifier.process(phrase);
            String predictedClass = prediction.isEmpty() ? "<INVALID>" : CYK.whichSkill(prediction);
            if(label.equals(predictedClass)){
                correct++;
            }
            System.out.println(correct + " / " + i);
        }
        double finalScore = correct/phrases.size();
        System.out.println("Final score: " + finalScore);
    }
}
