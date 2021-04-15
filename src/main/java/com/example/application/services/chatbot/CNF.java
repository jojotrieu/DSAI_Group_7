package com.example.application.services.chatbot;

import java.util.*;

public class CNF {
    private CFG cfg;
    private Map<String, List<String>> cnf;
    private Map<String, Integer> indexMap;

    public CNF(){
        cfg = new CFG();
        cfg.loadRules();
    }

    public CNF(CFG c){
        cfg=c;
    }

    public void initialize(){
        cnf = new HashMap<>();
        for(Rule rule : cfg.getRules()){
            cnf.put(rule.getVariable(), rule.getExpressions()); //TODO: find a way to clone those string string list
        }
        // eliminate unit rule
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                while (unary(entry.getValue().get(i))) {
                    entry.setValue(replace(entry.getValue(), i)); //TODO: verify if deleting unused token is ok -> do it if so
                }
            }
        }
        // all RHS are variable or terminal:


//        cnf.forEach((k,v) -> System.out.println(k+ " "+v));
        HashMap<String, List<String>> newVariables = new HashMap<>();
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                 if(entry.getValue().get(i).split(" ").length>1){
                     String[] line = entry.getValue().get(i).split(" ");
                     if(line.length == 2 && line[0].contains("<") && line[1].contains("<")) continue;
                     else {
                         String newWord = "";
                         for(String w : line){
                             if(!w.contains("<")) {
                                 String key = "<" + w + "PL>";
                                 newWord += key +" ";
                                 if (!newVariables.containsKey(key)) newVariables.put(key, toArrayList(w));
                             }else{
                                 newWord += w+" ";
                             }
                             entry.getValue().set(i, newWord.strip());
                              // getting rid of the last space
                         }
                     }
                 }else{ //normally all RHS should be terminal here
//                     System.out.println("rhs terminal?:" + entry.getValue().get(i));
                 }

            }
        }
        // all RHS non-terminal must be at most 2 of length:
//        for(Map.Entry<String, List<String>> entry:cnf.entrySet()) for(String w: entry.getValue()) System.out.println("pre processed: "+ w);
        int counter = 1;
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for(int i=0; i<entry.getValue().size();i++) {
                while(entry.getValue().get(i).split(" ").length>2){
                    String newline="<Y"+counter+"> ";
                    String firstTwo = firstTwo(entry.getValue().get(i));
                    newline+=entry.getValue().get(i).substring(firstTwo.length()+1);
                    newVariables.put("<Y"+counter+">", toArrayList(firstTwo));
                    counter++;
                    entry.getValue().set(i, newline);
//                    System.out.println(newline);
                }

            }
        }


        cnf.putAll(newVariables);
        initializeIndexMap();
    }

    private void initializeIndexMap(){
        indexMap = new HashMap<>();
        int i=0;
        for(Map.Entry<String, List<String>> entry:cnf.entrySet()){
            indexMap.put(entry.getKey(), i++);
//            System.out.println(entry.getKey());
        }
    }

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

    private List<String> toArrayList(String w) {
        ArrayList<String> result = new ArrayList<>();
        result.add(w);
        return result;
    }


    // replace the tag at index "i" with the RHS of corresponding tag
    public List<String> replace(List<String> line, int index){
//        System.out.println("value: "+ line.get(index));
//        System.out.println("replace with: " +cnf.get(line.get(index)));
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
    // TODO:create new HashMap subclass that supports indexation with int?? // OR just use a counter to keep track of the index
    public int[] yields(String word){
        ArrayList<Integer> result = new ArrayList<>();
        int index = 0;
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                if(entry.getValue().get(i).split(" ").length==1 && entry.getValue().get(i).equals(word)){
                    result.add(index);
//                    System.out.println(word+" "+index+ " ");
//                    for(Map.Entry<String, Integer> e: indexMap.entrySet()){
//                        if(e.getValue().equals(new Integer(index))) System.out.println("correspond to "+ e.getKey());
//                    }
                }
            }

            index++;
        }
        return toArray(result);
    }

    private int[] toArray(ArrayList<Integer> result) {
        int[] array = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            array[i]=result.get(i);
        }
        return array;
    }

    // return a list of indices of non terminal symbol[0] yielding 2 non terminal symbols[1,2]
    // Ra -> Rb Rc   return a b c
    public int[][] ruleYield(){
        ArrayList<int[]> result = new ArrayList<>();
        for(Map.Entry<String, List<String>> entry: cnf.entrySet()){
            for (int i = 0; i < entry.getValue().size(); i++) {
                if(entry.getValue().get(i).split(" ").length==2){
//                    System.out.println(entry.getKey());
//                    System.out.println(entry.getValue().get(i).split(" ")[0]+" "+entry.getValue().get(i).split(" ")[1]);
                    int[] a = new int[]{indexMap.get(entry.getKey()),
                            indexMap.get(entry.getValue().get(i).split(" ")[0]),
                            indexMap.get(entry.getValue().get(i).split(" ")[1])};
                    result.add(a);
//                    System.out.println(indexMap.get(entry.getKey())+" "+ entry.getKey());
//                    System.out.println(indexMap.get(entry.getValue().get(i).split(" ")[0])+" "+ entry.getValue().get(i).split(" ")[0]);
//                    System.out.println(indexMap.get(entry.getValue().get(i).split(" ")[1])+" "+ entry.getValue().get(i).split(" ")[1]);

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

    public boolean CYK(String query){
        //maybe some kind of preprocessing/normalization including what to do with coma
        String[] S = query.split(" ");

        boolean[][][] P = new boolean[S.length][S.length][getCnf().size()];

        for (int i = 0; i < S.length; i++) {
            for(int j: yields(S[i])){
                P[i][0][j]=true;
                System.out.println(i+" "+S[i]+" "+j+" ");
            }
        }
        for (int i = 1; i < S.length; i++) {
            for (int j = 0; j < S.length - i ; j++) {
                for (int k = 0; k < i ; k++) {
                    for (int[] abc: ruleYield()) {
                        if(P[j][k][abc[1]] && P[j+k+1][i-k-1][abc[2]])
                            P[j][i][abc[0]]=true;

                    }
                }
            }
        }
        for (int x = 0; x < getCnf().size(); x++) {
            if(P[0][S.length-1][x]) return true;
        }
        return false;
    }
}
