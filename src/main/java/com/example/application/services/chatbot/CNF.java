package com.example.application.services.chatbot;

import java.util.*;

public class CNF {
    private static Map<String, List<String>> cnf;
    private static Map<String, Integer> indexMap;
    private static String[] rules;
    private static final String PREFIX="zzzplhld";

    /**
     * create the cnf HashMap in order to use the CYK algorithm
     * should be called each timethere is a change in the cfg before
     * using CYK
     */
    public static void initialize(){
        cnf = new HashMap<>();
        for(Rule rule : CFG.getRulesCopy()){
            cnf.put(rule.getVariable(), rule.getExpressions());
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
//                if(entry.getValue().get(i).contains("><")) System.out.println("rhs variable or terminal");
                String[] line = splitRules(entry.getValue().get(i));
//                for(String h : line) if(h.contains("problem")) System.out.println(entry.getValue().get(i));
                 if(line.length>1){ // if RHS has more than a symbol (else it should be a terminal)
                     if(line.length == 2 && line[0].contains("<") && line[1].contains("<")) continue; //nothing to do it is already in the good form: 2 non terminal symbols
                     else {
                         String newWord = "";
                         for(String w : line){
                             if(!w.contains("<")) {
                                 String key = "<"+PREFIX + w + ">"; // -> new non terminal symbol
                                 newWord += key +" ";
                                 if (!newVariables.containsKey(key)) {
                                     newVariables.put(key, toArrayList(w));
                                 }
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
//                if(entry.getValue().get(i).contains("><")) System.out.println("last step");
                while(splitRules(entry.getValue().get(i)).length>2){
                    String newline="<"+PREFIX+"Y"+counter+"> ";
                    String firstTwo = firstTwo(entry.getValue().get(i)); // get the 2 first words
                    if(firstTwo.contains("><")) System.out.println("founded");
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
        }
    }

    /**
     * method that takes a string and return the first 2 words
     * @param s string that can have multiple words between brackets (variable) that counts as a word
     * @return the 2 first words as a single string
     */
    private static String firstTwo(String s) {
        int last=0;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)=='<'){ // we found the beginning of a variable
                for (int j = i; j < s.length(); j++) { //look for the closing braquet if it exists
                    if(s.charAt(j)=='>'){
                        if(last==0) last = i+1; // first word -> continue
                        else return s.substring(0,j+1); // second word -> return
                        i=j+1;
                        j+=s.length();
                    }
                }
            }else if(s.charAt(i)==' '){
                if(last == 0) last=i+1; //first word -> continue
                else return s.substring(0,i); //second word -> return
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
        String[] split = splitRules(tag.strip());
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

    public static String[] splitRules(String sentence){
        ArrayList<String> result = new ArrayList<>();
        int last=0;
        for (int i = 0; i < sentence.length(); i++) {
            if(sentence.charAt(i)=='<'){
                for (int j = i; j < sentence.length(); j++) {
                    if(sentence.charAt(j)=='>'){
//                        if(sentence.substring(i,j+1).contains(">") && !sentence.substring(i,j+1).contains("<")) System.out.println("in "+sentence.substring(i,j+1));
                        result.add(sentence.substring(i,j+1));
                        i=j+1;
                        j+=sentence.length();
                        last = i+1;
                    }
                }
            }else if(sentence.charAt(i)==' '){
                result.add(sentence.substring(last, i));
//                if(sentence.substring(last, i).contains(">") && !sentence.substring(last, i).contains("<")) {
//                    System.out.println("out "+sentence.substring(last, i)+" "+ last+ " " + i);
//                    System.out.println(sentence);
//                }
                last=i+1;
            }
        }
        if(last<sentence.length()) result.add(sentence.substring(last)); // if last thing added was not a variable
        String[] ret = new String[result.size()];
        return result.toArray(ret);
    }

    private static int next(String sentence, int index){
        for (int i = index; i < sentence.length(); i++) {
            if(sentence.charAt(i)==' ') return i+1;
            else if(sentence.charAt(i)=='<') return i;
        }
        return index+1;
    }
}
