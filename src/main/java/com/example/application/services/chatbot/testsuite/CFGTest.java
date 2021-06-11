package com.example.application.services.chatbot.testsuite;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.*;

import com.example.application.services.utils.TextFileIO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CFGTest {

    @Test
    void isVariable() {
        assert CFG.isVariable("<J>");
        assert !CFG.isVariable("<>");
        assert !CFG.isVariable("<JkK>");
        assert CFG.isVariable("<J5K>");
        assert !CFG.isVariable("<!KL>");
        assert !CFG.isVariable("JKADL");
    }

    @Test
    void upANDDown(){
        assert CFG.loadRules();
        for(Rule rule : CFG.getRules()){
            System.out.println("---------------------------------------------");
            System.out.println(rule.getVariable() + " - id#: " + rule.getId());
            List<Rule> up = CFG.up(rule);
            List<Rule> down = CFG.down(rule);
            System.out.println("UP:");
            for(Rule r : up){
                System.out.println(r.getVariable());
            }
            System.out.println("DOWN:");
            for(Rule r : down){
                System.out.println(r.getVariable());
            }
        }
    }

    @Test
    void splitExpressions(){
        CFG.loadRules();
        for(Rule rule : CFG.getRules()){
            System.out.println("---------------------------------------------");
            System.out.println(rule.getVariable() + " - id#: " + rule.getId());
            for(List<String> list : rule.getSplitExpressions()){
                for(String str : list){
                    System.out.print(str+" ");
                }
                System.out.println();
            }
        }
    }


    @Test
    void allPhrases(){
        CFG.loadRules();
        for(List<String> phrase : CFG.getAllPhrases()){
            for(String word : phrase ){
                System.out.print(word + " ");
            }
            System.out.println();
        }
    }
    @Test
    void allPhrasesAsQuery(){
        ChatBot.init();
        ArrayList<String> result = new ArrayList<>();
        for(List<String> phrase : CFG.getAllPhrases()){
            String query = "";
            for(String word : phrase ){
                query += word + " ";
            }
            result.add(query);
            result.add(ChatBot.respondTo(query));
            result.add(" ");
        }
        TextFileIO.write("testAll.txt", result);
    }

}