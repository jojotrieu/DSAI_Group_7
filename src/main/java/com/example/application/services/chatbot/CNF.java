package com.example.application.services.chatbot;

import java.util.*;
//TODO: rename this class? or restructure the code accordingly
public class CNF {
    private CFG cfg;
    private Map<String, List<String>> cnf;
    private Map<String, Integer> indexMap;
    private String[] rules;
    private String prefix="plhld";
    private Skills skills;
    private Map<String, String> placeHolders;
    private String action;

    public CNF(){
        cfg = new CFG();
        cfg.loadRules();
    }

    public CNF(CFG c){
        cfg=c;
    }

    /**
     * create the cnf HashMap in order to use the CYK algorithm
     * should be called each timethere is a change in the cfg before
     * using CYK
     */
    public void initialize(){
        cnf = new HashMap<>();
        for(Rule rule : cfg.getRules()){
            cnf.put(rule.getVariable(), rule.getExpressions()); //TODO: find a way to clone those string string list
        }
        // eliminate unit rule
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                while (unary(entry.getValue().get(i))) { //while there is a rule that yields non terminal symbol
                    entry.setValue(replace(entry.getValue(), i)); //TODO: verify if deleting unused token is ok -> do it if so
                } // replace with whatever the non terminal symbol yields
            }
        }
        // all RHS are variable or terminal:
        HashMap<String, List<String>> newVariables = new HashMap<>();
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                 if(entry.getValue().get(i).split(" ").length>1){ // if RHS has more than a symbol (else it should be a terminal)
                     String[] line = entry.getValue().get(i).split(" ");
                     if(line.length == 2 && line[0].contains("<") && line[1].contains("<")) continue; //nothing to do it is already in the good form: 2 non terminal symbols
                     else {
                         String newWord = "";
                         for(String w : line){
                             if(!w.contains("<")) {
                                 String key = "<"+prefix + w + ">"; // -> new non terminal symbol
                                 newWord += key +" ";
                                 if (!newVariables.containsKey(key)) newVariables.put(key, toArrayList(w));
                             }else{
                                 newWord += w+" ";
                             }
                             entry.getValue().set(i, newWord.strip()); // getting rid of the last space
                         }
                     }
                 }else{ //normally all RHS should be terminal here
//                     System.out.println("rhs terminal?:" + entry.getValue().get(i));
                 }

            }
        }
        // all RHS non-terminal must be at most 2 of length:
        int counter = 1; // counter to have a different name for each new variable
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                while(entry.getValue().get(i).split(" ").length>2){
                    String newline="<"+prefix+"Y"+counter+"> ";
                    String firstTwo = firstTwo(entry.getValue().get(i)); // get the 2 first words
                    newline+=entry.getValue().get(i).substring(firstTwo.length()+1); // copy the rest of the string into newline
                    newVariables.put("<"+prefix+"Y"+counter+">", toArrayList(firstTwo)); // put this new rule in the new hashmap
                    counter++;
                    entry.getValue().set(i, newline); // change the string in the original hashmap
                }

            }
        }
        cnf.putAll(newVariables);
        initializeIndexMap();
    }

    /**
     * make a new hashmap to remember the index of each rule
     */
    private void initializeIndexMap(){
        indexMap = new HashMap<>();
        int i=0;
        rules = new String[cnf.size()];
        for(Map.Entry<String, List<String>> entry:cnf.entrySet()){
            rules[i]=entry.getKey();
            indexMap.put(entry.getKey(), i++);

//            System.out.println(entry.getKey());
        }


    }

    /**
     * method that takes a string and return the first 2 words
     * @param s string that should be words separated by spaces
     * @return the 2 first words
     */
    private String firstTwo(String s) {
        boolean encountered = false;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == ' '){
                if(!encountered) encountered = true;
                else return s.substring(0,i);
            }
        }
        return null;
    }


    // replace the tag at index "i" with the RHS of corresponding tag
    public List<String> replace(List<String> line, int index){
        line.addAll( cnf.get(line.get(index) ));
        line.remove(index);
        return line;
    }

    // check if Symbol is unary aka alone
    public boolean unary(String tag){
        String[] split = tag.strip().split(" ");
        if(split.length>1) return false;
        return tag.contains("<");
    }

    public Map<String, List<String>> getCnf() {
        return cnf;
    }

    // if CNF yields word -> return index of rule
    public int[] yields(String word){
        ArrayList<Integer> result = new ArrayList<>();
        int index = 0;
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                if(entry.getValue().get(i).split(" ").length==1 && entry.getValue().get(i).equals(word)){
                    result.add(index);
                }
            }

            index++;
        }
        return toArray(result);
    }

    // return a list of indices of non terminal symbol[0] yielding 2 non terminal symbols[1,2]
    // Ra -> Rb Rc   return a b c
    public int[][] ruleYield(){
        ArrayList<int[]> result = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                if(entry.getValue().get(i).split(" ").length==2){
                    int[] a = new int[]{indexMap.get(entry.getKey()),
                            indexMap.get(entry.getValue().get(i).split(" ")[0]),
                            indexMap.get(entry.getValue().get(i).split(" ")[1])};
                    result.add(a);

                }
            }

        }

        return toArray(result, true);
    }

    private int[][] toArray(ArrayList<int[]> result, boolean temp) {
        int[][] array = new int[result.size()][3];
        for (int i = 0; i < result.size(); i++) {
            array[i] = result.get(i);
        }
        return array;
    }
    private List<String> toArrayList(String w) {
        ArrayList<String> result = new ArrayList<>();
        result.add(w);
        return result;
    }

    private int[] toArray(ArrayList<Integer> result) {
        int[] array = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            array[i]=result.get(i);
        }
        return array;
    }

    private static boolean isStringUpperCase(String str){

        //convert String to char array
        char[] charArray = str.toCharArray();

        for(int i=0; i < charArray.length; i++){

            //if any character is not in upper case, return false
            if( !Character.isUpperCase( charArray[i] ))
                return false;
        }

        return true;
    }

    /**
     * CYK algo: check whether a query string is in the language of the CFG
     * @param query the question the user ask
     * @return true if so, false otherwise
     */
    public boolean CYK(String query){
        //TODO:maybe some kind of preprocessing/normalization including what to do with coma, '?', '.'
        String[] S = query.split(" ");
        placeHolders = new HashMap<>();
        boolean[][][] P = new boolean[S.length][S.length][getCnf().size()];

        for (int i = 0; i < S.length; i++) {
            for(int j: yields(S[i])){
                P[i][0][j]=true;
                if(isStringUpperCase(rules[j].substring(1,rules[j].length()-1))) placeHolders.put(rules[j],S[i]); // we got the DAY and TIME
            }
        }
        action = "";
        for (int i = 1; i < S.length; i++) {
            for (int j = 0; j < S.length - i ; j++) {
                for (int k = 0; k < i ; k++) {
                    for (int[] abc: ruleYield()) {
                        if(P[j][k][abc[1]] && P[j+k+1][i-k-1][abc[2]]) {
                            P[j][i][abc[0]] = true;
                            if(rules[abc[0]].contains("ACTION"))  // retrieve the appropriate action
                                action+= rules[abc[1]]+" "+rules[abc[2]]; //TODO: change the hardcoded "ACTION"?
                            if(isStringUpperCase(rules[abc[0]].substring(1,rules[abc[0]].length()-1 ))) placeHolders.put(rules[abc[0]], rules[abc[1]]+ " "+rules[abc[2]]);

                        }
                    }
                }
            }
        }


        for (int x = 0; x < getCnf().size(); x++) {
            if(P[0][S.length-1][x]) return true;
        }
        return false;
    }

    /**
     * method to be called to retrieve the answer
     * @return answer action in String variable
     */
    public String getAnswer(String query) {
        if (CYK(query)) {
            while (action.contains(prefix)) { // while some of the placeholder are made up by CNF new variables
                String[] arguments = action.split(" ");
                String nAction = "";
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i].contains(prefix)) {
                        nAction += cnf.get(arguments[i]).get(0); // replace those with
                    } else {
                        nAction += arguments[i];
                    }
                    if (i < arguments.length - 1) nAction += " ";
                }
                action = nAction;
            }
//            System.out.println(action);
            String actionVariable = null;

            CFG.loadRules();
            for (Rule r : cfg.getRules()) {
                for (String s : r.getExpressions()) {
                    if (s.equals(action)) actionVariable = r.getVariable();
                }
            }
            /**
             * this piece of code retrieve the answer
             */
            Skills.loadActions(); // TODO: oh no needs to be clone !
            skills = new Skills();

//            System.out.println("yooo: "+actionVariable);
            for (Action a : skills.getActions()) {
                if (a.getVariable().equals(actionVariable)) {
                    String[] expression = a.getExpression().split(" ");
                    boolean found = true;
                    for (int i = 0; i < expression.length; i++) { //check each word of the expression

                        if (CFG.isVariable(expression[i])) { // if it is a <VARIABLE>
                            if (placeHolders.containsKey(expression[i]) && // if it's recorded in placeHolders
                                    !placeHolders.get(expression[i]).equals(expression[i + 1]))
                                found = false; // but not the same value
                            else if (!placeHolders.containsKey(expression[i]))
                                found = false; // if it is not recorded in placeHolders...????????
                        }
                    }
                    if (found) return a.getExpression(); //TODO: take the substring of the answer without parameters
                }
            }
        } return"I don't know";
    }
}
