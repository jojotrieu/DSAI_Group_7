package com.example.application.services.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CYK {

    private static Map<String, String> placeHolders;
    private static String action;
    /**
     * CYK algo: check whether a query string is in the language of the CFG
     * @param query the question the user ask
     * @return true if so, false otherwise
     */
    public static boolean isValidLanguage(String query){
        //TODO:maybe some kind of preprocessing/normalization including what to do with coma, '?', '.'
        String[] rules = CNF.getRules();
        String[] S = query.split(" ");
        placeHolders = new HashMap<>();
        boolean[][][] P = new boolean[S.length][S.length][CNF.getCnf().size()];

        for (int i = 0; i < S.length; i++) {
            for(int j: yields(S[i])){
                P[i][0][j]=true;
                if(isStringUpperCase(rules[j].substring(1,rules[j].length()-1))) {
                    placeHolders.put(rules[j], S[i]); // we got the DAY and TIME
                }
            }
        }
        action = "";
        for (int i = 1; i < S.length; i++) {
            for (int j = 0; j < S.length - i ; j++) {
                for (int k = 0; k < i ; k++) {
                    for (int[] abc: ruleYield()) {
                        if(P[j][k][abc[1]] && P[j+k+1][i-k-1][abc[2]]) {
                            P[j][i][abc[0]] = true;
                            if(rules[abc[0]].contains("ACTION")) {
                                // retrieve the appropriate action
                                action+= rules[abc[1]]+" "+rules[abc[2]]; //TODO: change the hardcoded "ACTION"?
                            }
                            if(isStringUpperCase(rules[abc[0]].substring(1,rules[abc[0]].length()-1 ))) {
                                placeHolders.put(rules[abc[0]], rules[abc[1]] + " " + rules[abc[2]]);
                            }
                        }
                    }
                }
            }
        }


        for (int x = 0; x < CNF.getCnf().size(); x++) {
            if(P[0][S.length-1][x]) return true;
        }
        return false;
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

    // return a list of indices of non terminal symbol[0] yielding 2 non terminal symbols[1,2]
    // Ra -> Rb Rc   return a b c
    public static int[][] ruleYield(){
        Map<String, Integer> indexMap = CNF.getIndexMap();
        ArrayList<int[]> result = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: CNF.getCnf().entrySet()){
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

    private static int[][] toArray(ArrayList<int[]> result, boolean temp) {
        int[][] array = new int[result.size()][3];
        for (int i = 0; i < result.size(); i++) {
            array[i] = result.get(i);
        }
        return array;
    }

    // if CNF yields word -> return index of rule
    public static int[] yields(String word){
        ArrayList<Integer> result = new ArrayList<>();
        int index = 0;
        for(Map.Entry<String, List<String>> entry: CNF.getCnf().entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                if(entry.getValue().get(i).split(" ").length==1 && entry.getValue().get(i).equals(word)){
                    result.add(index);
                }
            }

            index++;
        }
        return toArray(result);
    }

    private static int[] toArray(ArrayList<Integer> result) {
        int[] array = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            array[i]=result.get(i);
        }
        return array;
    }

    public static Map<String, String> getPlaceHolders() {
        return placeHolders;
    }

    public static String getAction() {
        return action;
    }

    public static void setAction(String action) {
        CYK.action = action;
    }
}
