package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;

import java.util.*;

public class SyntaxHandler {
    private static String errorMessage;
    private static ArrayList<Set<String>> variables;
    private static Set<String> commonV; //TODO this needs to be updated no matter what

    /**
     * check the list of question handed from the UI
     * Check empty quesitons, variables syntax (<var>)
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
        checkForVariables(texts, true,0);
        for(String s: variables.get(0)){
            for(Rule r : CFG.getRules()){
                if(s.equals(r.getVariable())){ // TODO find them to propose?
                    errorMessage += "Rule name already used for " + s +", please change the name?\n";
                }
            }
        }

        if(errorMessage.length()==0 || errorMessage.startsWith("Rule name")){
            variables.add(new HashSet<>());
            return true;
        }

        return false;
    }

    /**
     * check variable syntax (same as questions)
     * @param values possible replacement of the variable in CFG (one list per variable)
     * @return true if everything is correct
     */
    public static boolean checkVariables(List<String> values, int page){
        errorMessage = "";
        if(variables.size()<page){
            variables.add(new HashSet<>());
        }else if(page == variables.size()){
            variables.set(page, new HashSet<>());
        }
        int lineCounter = 1;
        HashSet<String> firstVar=null;
            for (String value : values) {
                if (value != null) {
                    checkVarSyntax(value, lineCounter, "value");
                    lineCounter++;
                }
                if (value.equals("")) {
                    errorMessage += "Empty value line n" + lineCounter + " \n";
                }
            }
            checkForVariables(values, false, page);
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
    private static void checkVarSyntax(String line, int lineCounter, String valOrQ){
        boolean open = false;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '<') {
                if (!open) open = true;
                else {
                    errorMessage += "Double opened angle brackets at " +valOrQ + lineCounter + "\n";
                }
            } else if (line.charAt(i) == '>') {
                if (open){
                    open = false;
                }
                else {
                    errorMessage += "Unopened angle brackets at " +valOrQ + lineCounter + "\n";
                }
            }
        }
        if (open) {
            errorMessage += "Unclosed angle bracket at " +valOrQ + lineCounter + "\n";
        }
    }

    /**
     * check if there are any common variable in lines
     * also add variable to static variables at appropriate page
     * @param lines
     * @param question
     * @param page
     */
    private static void checkForVariables(List<String> lines, boolean question, int page){
        int i=1; //counter for lines
        HashSet<String> var = new HashSet<>(); // set of vars for the lines
        for(String line:lines){
            String[] splitLine = CNF.splitRules(line);
            HashSet<String> linevar = new HashSet<>();
            for(String w:splitLine){
                if(CFG.isVariable(w)){
//                    hasAtLeastone=true;
                    variables.get(page).add(w);
                    if(i==1) var.add(w);
                    else linevar.add(w);
                }
            }
            //if(i!=1) for(String w: var) if(!linevar.contains(w)) var.remove(w);
            i++;
        }
//        if(var.size()==0 && hasAtLeastone) errorMessage+="No common variable"; //TODO: ok no common variable ?
        if(question) commonV = var;
    } //TODO checkifanycommon for variabla : you need to check if an old common is replaced with new variables

    /**
     * find the common var from all input
     * @param allLines every input
     * @return list of common variable to make answers
     */
    public static Set<String> findCommonV(Map<String, List<String>> allLines){
        int i=0;
        Set<String> common = new HashSet<>();

        for(String keyVariable:allLines.keySet()){
            int j=0;
            Set<String> firstLineV = new HashSet<>();
            Set<String> currentV = new HashSet<>();
            Set<String> commonCurrent = new HashSet<>();
            if(i==0 || common.contains(keyVariable)){
                for(String line: allLines.get(keyVariable)){
                    String[] split = CNF.splitRules(line);
                    for(String w: split){ // check the line
                        if(CFG.isVariable(w)){
                            if(j==0) { // add to variables from first line
                                firstLineV.add(w);
                            }else if(j==1) { // second line compare to 1st
                                if(firstLineV.contains(w));
                                commonCurrent.add(w);
                            }else{
                                currentV.add(w);
                            }
                        }
                    }
                    if(j>1){ // third line comparison
                        currentV.retainAll(commonCurrent);
                    }
                    j++;
                }

            }
            if(currentV.size()==0) currentV = firstLineV;
            if(j==1) commonCurrent=firstLineV; // if only 1 line
            if(i==0) common = commonCurrent;
            else{
                if(commonCurrent.size()!=0){
                    common.remove(keyVariable);
                }
                common.addAll(commonCurrent);
                common.addAll(currentV);
            }
            i++;
        }
        return common;
    }
/*
    public static void main(String[] args){
        Map<String, List<String>> test= new HashMap<>();
        List<String> firstLine = new ArrayList<>();
        firstLine.add("<LIEU> hello");
        firstLine.add(" hello <LIEU>");
        test.put("<Skill test>", firstLine);
        List<String> secondLine = new ArrayList<>();
        secondLine.add("dany");
        secondLine.add("<ILE>");
        test.put("<LIEU>",secondLine);
        List<String> thirdLine = new ArrayList<>();
        thirdLine.add("<CANARIE>");
        thirdLine.add("jo");
        test.put("<ILE>", thirdLine);
        List<String> fourthLine = new ArrayList<>();
        fourthLine.add("blah");
        fourthLine.add("jo");
        test.put("<CANARIE>", fourthLine);
        Set ans = findCommonV(test);
        for(Object s : ans) System.out.println(s);
    }
    
 */
}
