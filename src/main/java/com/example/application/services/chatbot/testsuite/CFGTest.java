package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

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
        CFG cfg = new CFG();
        cfg.loadRules();
        for(Rule rule : cfg.getRules()){
            System.out.println("---------------------------------------------");
            System.out.println(rule.getVariable() + " - id#: " + rule.getId());
            List<Rule> up = cfg.up(rule);
            List<Rule> down = cfg.down(rule);
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