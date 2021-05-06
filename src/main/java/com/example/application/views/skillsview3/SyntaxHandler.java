package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;

import java.util.*;

public class SyntaxHandler {
    private static String errorMessage;
    private static Set<String> variables;
    private static Set<String> commonV;

    public static boolean checkQuestions(List<String> texts){
        variables = new HashSet<>();
        errorMessage = "";
        int lineCounter = 1;
        HashSet<String> firstVar = new HashSet<>();
        for(String line: texts){
            if(line.equals("")) {
                errorMessage += "Empty question n"+lineCounter+" \n";
            }
            if(line!=null) {
                boolean open = false;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        if (!open) open = true;
                        else {
                            errorMessage += "Double opened angle brackets at question " + lineCounter + "\n";
                        }
                    } else if (line.charAt(i) == '>') {
                        if (open) open = false;
                        else {
                            errorMessage += "Unopened angle brackets at question " + lineCounter + "\n";
                        }
                    }
                }
                if (open) {
                    errorMessage += "Unclosed angle bracket at question " + lineCounter + "\n";
                }
                String[] atomicArray = CNF.splitRules(line);
                boolean common = false;
                for (String atomic : atomicArray) {
                    if (atomic.charAt(0) == '<' && atomic.charAt(atomic.length() - 1) == '>') {
                        variables.add(atomic);
                        if(lineCounter==1) {
                            firstVar.add(atomic);
                            common =true;
                        }else{
                            for(String fv:firstVar){
                                if(atomic.equals(fv)) common = true;
                            }
                        }
                    }else if(lineCounter==1){
                        common = true;
                    }
                }
                if(!common && !errorMessage.contains("Missing") && !(lineCounter == 1 && errorMessage.contains("Empty"))){
                    errorMessage += "Missing at least one common variable for each question\n";
                }
                lineCounter++;
            }
        }
        for(String s: variables){
            for(Rule r : CFG.getRules()){
                if(s.equals(r.getVariable())){//TODO find them
                    errorMessage += "Rule name already used for " + s +"\n";
                }
            }
        }
        if(!errorMessage.contains("Missing")){
            commonV = new HashSet<>();
            for (int i = 1; i < texts.size(); i++) {
                for(String atomic1:CNF.splitRules(texts.get(0))){
                    boolean found = false;
                    for(String atomici: CNF.splitRules(texts.get(i))){
                        if(atomici.equals(atomic1)){
                            found = true;
                        }
                    }
                    if(found) commonV.add(atomic1);
                }
            }
        }
        if(errorMessage.length()==0 || errorMessage.startsWith("Rule name"))
            return true;
        return false;
    }

    public static Set<String> getVariables(){
        return variables;
    }


    public static String getErrorMessage(){
        return errorMessage;
    }

}
