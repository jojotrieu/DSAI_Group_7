package com.example.application.services.chatbot;

import java.util.*;

public class CNF {
    private CFG cfg;
    private Map<String, List<String>> cnf;

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
                    entry.setValue(replace(entry.getValue(), i));
                }
            }
        }
        // all RHS are variable or terminal
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
        return null;
    }

    // return a list of indices of non terminal symbol[0] yielding 2 non terminal symbols[1,2]
    // Ra -> Rb Rc   return a b c
    public int[][] ruleYield(){
        return null;
    }

    public boolean CYK(String query){
        //maybe some kind of preprocessing/normalization including what to do with coma
        String[] S = query.split(" ");

        boolean[][][] P = new boolean[S.length][S.length][getCnf().size()];

        for (int i = 0; i < S.length; i++) {
            for(int j: yields(S[i])){ P[i][0][j]=true;}
        }
        for (int i = 1; i < S.length; i++) {
            for (int j = 0; j < S.length - i + 1; j++) {
                for (int k = 0; k < i - 1; k++) {
                    for (int[] abc: ruleYield()) {
                        if(P[j][k][abc[1]] && P[j+k][i-k][abc[2]]) P[i][j][abc[0]]=true;
                    }
                }
            }
        }
        for (int x = 0; x < getCnf().size(); x++) {
            if(P[0][S.length][x]) return true;
        }
        return false;
    }
}
