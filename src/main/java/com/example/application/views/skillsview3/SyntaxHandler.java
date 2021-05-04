package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;

import java.util.*;

public class SyntaxHandler {
    private static String errorMessage;

    public static boolean checkQuestions(List<String> texts){
        Set<String> variables = new HashSet<>();
        errorMessage = "";
        int lineCounter = 1;
        for(String line: texts){
            boolean open = false;
            for(int i=0;i<line.length();i++){
                if(line.charAt(i) == '<'){
                    if(!open) open = true;
                    else{
                        errorMessage += "Double opened angle brackets at question " + lineCounter +"\n";
                    }
                }else if(line.charAt(i) == '>'){
                    if(open) open = false;
                    else{
                        errorMessage += "Double closed angle brackets at question " + lineCounter +"\n";
                    }
                }
            }
            if(open){
                errorMessage += "Unclosed angle bracket at question " + lineCounter +"\n";

            }
            String[] atomicArray = CNF.splitRules(line);
            for(String atomic : atomicArray){
                if(atomic.charAt(0)=='<' && atomic.charAt(atomic.length()-1)=='>'){
                    variables.add(atomic);
                }
            }
            lineCounter++;
        }
        for(String s: variables){
            for(Rule r : CFG.getRules()){
                if(s.equals(r.getVariable())){
                    errorMessage += "Rule name already used for " + s +"\n";
                }
            }
        }
        if(errorMessage.length()==0 || errorMessage.startsWith("Rule name"))
            return true;
        return false;
    }



    public static String getErrorMessage(){
        return errorMessage;
    }

}
