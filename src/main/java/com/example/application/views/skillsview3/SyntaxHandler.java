package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;

import java.io.FileNotFoundException;
import java.util.*;

public class SyntaxHandler {
    private static String errorMessage;
    private static ArrayList<Set<String>> variables;
    private static Set<String> commonV; //TODO this needs to be updated no matter what

    /**
     * check the list of question handed from the UI
     * Check empty questions, variables syntax (<var>)
     * @param texts list of questions related to the skill
     * @return true if everything is written correctly
     */
    public static boolean checkQuestions(List<String> texts){
        variables = new ArrayList<>();
        variables.add(new HashSet<>());
        errorMessage = "";
        int lineCounter = 1;
        HashSet<String> firstVar=null;
        for(String line: texts){
            if(line.equals("")) errorMessage += "Empty question n"+lineCounter+" \n";
            if(line!=null) {
                checkVarSyntax(line, lineCounter, "question");
                String[] atomicArray = CNF.splitRules(line);
//                firstVar = checkCommon(atomicArray,firstVar,lineCounter);
                lineCounter++;
            }
            if(countVariables(line) > SkillsView3.limit){
                errorMessage += "Too many variables per question \n";
            }
        }
        checkForVariables(texts, true,0);
        for(String s: variables.get(0)){
            for(Rule r : CFG.getRules()){
                if(s.equals(r.getVariable())) errorMessage += "Rule name already used for " + s +", please change the name?\n";
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
                if (value.equals("")) errorMessage += "Empty value line n" + lineCounter + " \n";
            }
            checkForVariables(values, false, page);
        if(errorMessage.equals("")) return true;
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
                else  errorMessage += "Double opened angle brackets at " +valOrQ + lineCounter + "\n";
            } else if (line.charAt(i) == '>') {
                if (open) open = false;
                else errorMessage += "Unopened angle brackets at " +valOrQ + lineCounter + "\n";
            }
        }
        if (open) errorMessage += "Unclosed angle bracket at " +valOrQ + lineCounter + "\n";
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
                    variables.get(page).add(w);
                    if(i==1) var.add(w);
                    else linevar.add(w);
                }
            }
            i++;
        }
        if(question) commonV = var;
    } //TODO checkifanycommon for variable : you need to check if an old common is replaced with new variables

    public static int countVariables(String line) {
        String[] splitLine = CNF.splitRules(line);
        int count = 0;
        for (String w : splitLine) {
            if (CFG.isVariable(w)) {
                count++;
            }
        }
        return count;
    }

    /**
     * find the common var from all input
     * @param allLines every input
     * @return list of common variable to make answers
     */
    public static List<Set<String>> findCommonV(Map<String, List<String>> allLines){
        int i=0;
        List<Set<String>> result = new ArrayList<>();
        result.add(new HashSet<>());
        result.add(new HashSet<>());
//        Set<String> common = new HashSet<>();

        for(String keyVariable:allLines.keySet()){
            int j=0;
            Set<String> firstLineV = new HashSet<>();
            Set<String> currentV = new HashSet<>();
            Set<String> commonCurrent = new HashSet<>();
            if(i==0 || result.get(0).contains(keyVariable) || result.get(1).contains(keyVariable)){
                for(String line: allLines.get(keyVariable)){
                    String[] split = CNF.splitRules(line);
                    for(String w: split){ // check the line
                        if(CFG.isVariable(w)){
                            if(j==0) { // add to variables from first line
                                firstLineV.add(w);
                            }else if(j==1) { // second line compare to 1st
                                if(firstLineV.contains(w)) commonCurrent.add(w);
                            }else{
                                currentV.add(w);
                            }
                            if(result.get(0).contains(keyVariable) ||result.get(1).contains(keyVariable)) currentV.add(w);
                        }
                    }
                    if(j>1){ // third line comparison
                        commonCurrent.retainAll(currentV);
                    }
                    j++;
                }

            }
            if(currentV.size()==0) currentV = firstLineV;
            if(j==1) commonCurrent=firstLineV; // if only 1 line
            if(i==0) result.set(0,commonCurrent);
            else{
                if(commonCurrent.size()!=0){
                    result.get(0).remove(keyVariable);
                }
                result.get(0).addAll(commonCurrent);
                for(String s: currentV) {
                    if(!result.get(0).contains(s))
                        result.get(1).add(s);
                }
            }
            i++;
        }
        return result;
    }

    public static void saveRules(Map<String, List<String>> allLines){
        CFG.loadRules();
        List<Rule> allRules = CFG.getRules();
        String actionVariable = null;
        for(Rule r: allRules) {
            if(r.getVariable().equals("<S>")) {
                actionVariable = r.getExpressions().get(0);
            }
        }
        boolean titleAdd = false;
        for(String key:allLines.keySet()) {
            boolean added = false;
            for (Rule rule : allRules) {
                if(!titleAdd && rule.getVariable().equals(actionVariable)){
                    rule.getExpressions().add(key);
                    titleAdd=true;
                }else if(rule.getVariable().equals(key)){
                    for(String s: allLines.get(key)){
                        if(!rule.getExpressions().contains(s)) rule.getExpressions().add(s);
                    }
                    added=true;
                }

            }
            if(!added){
                Rule newRule = new Rule();
                newRule.setVariable(key);
                newRule.setExpressions(allLines.get(key));
                allRules.add(newRule);
            }
        }
        try{
            CFG.writeRules();
        }catch (FileNotFoundException exception){
            exception.printStackTrace();
        }
    }

    public static void saveActions(String title, List<String> answers, ArrayList<ArrayList<Object>> varVal) {
        Skills.loadActions();

        if(SkillsView3.noVar){

            Action newAction = new Action();
            newAction.setId(Skills.getActions().size() - 1);
            newAction.setVariable(title);
            Map<String, String> nt = new HashMap<>();
            newAction.setNonTerminals(nt);
            newAction.setExpression(answers.get(0));
            Skills.getActions().add(newAction);

        }else{

            for (int i = 0; i < answers.size(); i++) {
                Action newAction = new Action();
                newAction.setId(Skills.getActions().size() - 1);
                newAction.setVariable(title);
                Map<String, String> nt = new HashMap<>();
                if(varVal.get(i).size()==2){
                    Label l0 = (Label) varVal.get(i).get(0);
                    Label l1 = (Label) varVal.get(i).get(1);
                    nt.put(l0.getText(), l1.getText());
                }else{
                    for(int j = 0; j<varVal.get(i).size() ; j+=2){
                        Label l = (Label) varVal.get(i).get(j);
                        ComboBox cb = (ComboBox) varVal.get(i).get(j+1);
                        nt.put(l.getText(), cb.getValue().toString());
                    }
                }
                newAction.setNonTerminals(nt);
                newAction.setExpression(answers.get(i));

                Skills.getActions().add(newAction);
            }
        }
        try{
            Skills.writeActions();
        }catch (FileNotFoundException exception){
            exception.printStackTrace();
        }
    }
    
    public static Set<String> checkIfAnyCommonInLines(List<String> lines){
        Set<String> res = new HashSet<>();
        Set<String> current = null;
        for(String line:lines){
            for(String w : CNF.splitRules(line)){
                if(CFG.isVariable(w)){
                    if(current == null) res.add(w);
                    else current.add(w);
                }
            }
            if(current == null) current = new HashSet<>();
            else res.retainAll(current);
        }
        return res;
    }

    public static void main(String[] args){
        Map<String, List<String>> test= new LinkedHashMap<>();
        List<String> firstLine = new ArrayList<>();
        firstLine.add("<LIEU> hello");
        firstLine.add(" hello <LIEU>");
        test.put("<Skill test>", firstLine);
        List<String> secondLine = new ArrayList<>();
        secondLine.add("dany <ILE>");
        secondLine.add("da ");
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
        List<Set<String>> ans = findCommonV(test);
        for(Set<String> a:ans) System.out.println(a);
    }

}
