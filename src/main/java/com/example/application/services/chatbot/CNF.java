package com.example.application.services.chatbot;

import java.util.*;
//TODO: rename this class? or restructure the code accordingly
public class CNF {
    private static Map<String, List<String>> cnf;
    private static Map<String, Integer> indexMap;
    private static String[] rules;
    private static final String PREFIX="plhld";

    /**
     * create the cnf HashMap in order to use the CYK algorithm
     * should be called each timethere is a change in the cfg before
     * using CYK
     */
    public static void initialize(){
        cnf = new HashMap<>();
        for(Rule rule : CFG.getRulesCopy()){
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
                                 String key = "<"+PREFIX + w + ">"; // -> new non terminal symbol
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
                    String newline="<"+PREFIX+"Y"+counter+"> ";
                    String firstTwo = firstTwo(entry.getValue().get(i)); // get the 2 first words
                    newline+=entry.getValue().get(i).substring(firstTwo.length()+1); // copy the rest of the string into newline
                    newVariables.put("<"+PREFIX+"Y"+counter+">", toArrayList(firstTwo)); // put this new rule in the new hashmap
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
    private static void initializeIndexMap(){
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
    private static String firstTwo(String s) {
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
    public static List<String> replace(List<String> line, int index){
        line.addAll( cnf.get(line.get(index) ));
        line.remove(index);
        return line;
    }

    // check if Symbol is unary aka alone
    public static boolean unary(String tag){
        String[] split = tag.strip().split(" ");
        if(split.length>1) return false;
        return tag.contains("<");
    }

    public static Map<String, List<String>> getCnf() {
        return cnf;
    }

    private static List<String> toArrayList(String w) {
        ArrayList<String> result = new ArrayList<>();
        result.add(w);
        return result;
    }

    public static Map<String, Integer> getIndexMap() {
        return indexMap;
    }

    public static String[] getRules() {
        return rules;
    }

    public static String getPREFIX() {
        return PREFIX;
    }
}
