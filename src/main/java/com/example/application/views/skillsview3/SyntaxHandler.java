package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;

import java.util.*;

public class SyntaxHandler {
    private static String errorMessage;
    private static ArrayList<Set<String>> variables;
    private static Set<String> commonV;

    /**
     * check the list of question handed from the UI
     * Check empty quesitons, variables syntax (<var>)
     * at least one common variable if any avirable at all
     * @param texts list of questions related to the skill
     * @return true if everythin is written correctly
     */
    public static boolean checkQuestions(List<String> texts){
        variables = new ArrayList<>();
        variables.add(new HashSet<>());
        errorMessage = "";
        int lineCounter = 1;
        HashSet<String> firstVar=null;
        for(String line: texts){
            if(line.equals("")) {
                errorMessage += "Empty question n"+lineCounter+" \n";
            }
            if(line!=null) {
                checkVarSyntax(line, lineCounter, "question");
                String[] atomicArray = CNF.splitRules(line);

//                firstVar = checkCommon(atomicArray,firstVar,lineCounter);
                lineCounter++;
            }
        }
        checkIfAnyCommon(texts, true,0);
        for(String s: variables.get(0)){
            for(Rule r : CFG.getRules()){
                if(s.equals(r.getVariable())){ // TODO find them to propose?
                    errorMessage += "Rule name already used for " + s +", please change the name?\n";
                }
            }
        }

        if(errorMessage.length()==0 || errorMessage.startsWith("Rule name"))
            return true;
        return false;
    }

    /**
     * check variable syntax (same as questions)
     * @param variable variable that takes the values of "values"
     * @param values possible replacement of the variable in CFG
     * @return true if everything is correct
     */
    public static boolean checkVariables(String variable, List<String> values, int page){
        errorMessage = "";
        if(page>=variables.size()) variables.add(new HashSet<>());
        int lineCounter = 1;
        boolean hasV=false;
        HashSet<String> firstVar=null;
        for(String value:values){
            if(value!=null){
                hasV = checkVarSyntax(value, lineCounter, "value");
                lineCounter++;
            }
        }
        if(hasV){
            if(commonV.contains(variable)){
                checkIfAnyCommon(values, false, page);
            }
        }
        if(errorMessage.equals("")) {
            return true;
        }
        return false;
    }


    public static Set<String> getVariables(int page){
        return variables.get(page);
    }

    public static String getErrorMessage(){
        return errorMessage;
    }

    /**
     * checking the syntax of the variable with angle brackets
     * @param line the line to verify
     * @param lineCounter if it is the 1st line or no -> also used for error message
     * @param valOrQ if we are verifying for question or value
     * @return
     */
    private static boolean checkVarSyntax(String line, int lineCounter, String valOrQ){
        boolean open = false;
        boolean ret = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '<') {
                if (!open) open = true;
                else {
                    errorMessage += "Double opened angle brackets at " +valOrQ + lineCounter + "\n";
                }
            } else if (line.charAt(i) == '>') {
                if (open){
                    open = false;
                    ret = true;
                }
                else {
                    errorMessage += "Unopened angle brackets at " +valOrQ + lineCounter + "\n";
                }
            }
        }
        if (open) {
            errorMessage += "Unclosed angle bracket at " +valOrQ + lineCounter + "\n";
        }
        return ret;
    }

    private static void checkIfAnyCommon(List<String> lines, boolean question, int page){
        int i=1;
        HashSet<String> var = new HashSet<>();
        boolean hasAtLeastone = false;
        for(String line:lines){
            String[] splitLine=CNF.splitRules(line);
            HashSet<String> linevar = new HashSet<>();
            for(String w:splitLine){
                if(CFG.isVariable(w)){
                    hasAtLeastone=true;
                    variables.get(page).add(w);
                    if(i==1) var.add(w);
                    else linevar.add(w);
                }
            }
            if(i!=1) for(String w: var) if(!linevar.contains(w)) var.remove(w);
            i++;
        }
        if(var.size()==0 && hasAtLeastone) errorMessage+="No common variable";
        if(question) commonV = var;
    }

}
