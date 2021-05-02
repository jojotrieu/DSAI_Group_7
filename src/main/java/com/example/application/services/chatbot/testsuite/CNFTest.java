package com.example.application.services.chatbot.testsuite;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.chatbot.Rule;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CNFTest {

    @Test
    public void isCNF(){
        CNF.initialize();
        for(Map.Entry<String, List<String>> entry: CNF.getCnf().entrySet()){
            for(String rule: entry.getValue()){
                if(rule.split(" ").length == 2){
                    assert CFG.isVariable(rule.split(" ")[0]);
                    assert CFG.isVariable(rule.split(" ")[1]);
                }else{
                    assert !CFG.isVariable(rule);
                }
            }
        }
    }

    @Test
    public void splitRulesMethod(){
        String var = "<var 1> has no >_< idea what's going on";
        assert CNF.splitRules(var).length == 8;
    }


}
