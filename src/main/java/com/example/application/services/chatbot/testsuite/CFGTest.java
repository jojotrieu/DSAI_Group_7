package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

import java.util.List;

class CFGTest {

    @Test
    void isVariable() {
        assert com.example.application.services.chatbot.CFG.isVariable("<J>");
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



}