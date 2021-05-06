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
        for(String line: texts){
            if(line.equals("")) {
                errorMessage += "Empty question n"+lineCounter+" \n";
            }
            if(line!=null) {
                checkVarSyntax(line, lineCounter, "question");
                String[] atomicArray = CNF.splitRules(line);
                checkCommon(atomicArray,lineCounter);
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
            findCommon(texts, true);
        }
        if(errorMessage.length()==0 || errorMessage.startsWith("Rule name"))
            return true;
        return false;
    }

    public static boolean checkVariables(String variable, List<String> values){
        errorMessage = "";
        int lineCounter = 1;
        boolean hasV=false;
        for(String value:values){
            if(value!=null){
                hasV = checkVarSyntax(value, lineCounter, "value");
                lineCounter++;
            }
        }
        if(hasV){
            if(commonV.contains(variable)){
                int counter=1;
                for(String value:values){
                    if(value!=null) {
                        String[] splitV = CNF.splitRules(value);
                        checkCommon(splitV, counter);
                        counter++;
                    }
                }
            }
        }
        if(errorMessage.equals("")) return true;
        return false;
    }

    public static Set<String> getVariables(){
        return variables;
    }


    public static String getErrorMessage(){
        return errorMessage;
    }

    private static boolean checkVarSyntax(String line, int lineCounter, String varOrQ){
        boolean open = false;
        boolean ret = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '<') {
                if (!open) open = true;
                else {
                    errorMessage += "Double opened angle brackets at " +varOrQ + lineCounter + "\n";
                }
            } else if (line.charAt(i) == '>') {
                if (open){
                    open = false;
                    ret = true;
                }
                else {
                    errorMessage += "Unopened angle brackets at " +varOrQ + lineCounter + "\n";
                }
            }
        }
        if (open) {
            errorMessage += "Unclosed angle bracket at " +varOrQ + lineCounter + "\n";
        }
        return ret;
    }

    private static void checkCommon(String[] splitLine, int lineCounter){
        HashSet<String> firstVar = new HashSet<>();
        boolean common = false;
        for (String atomic : splitLine) {
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
            }else if(firstVar.size() == 0){
                common = true;
            }
        }
        if(!common && !errorMessage.contains("Missing") && !(lineCounter == 1 && errorMessage.contains("Empty"))){
            errorMessage += "Missing at least one common variable for each question\n";
        }
    }

    private static void findCommon(List<String> texts, boolean first){
        if(first) commonV = new HashSet<>();
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
}
