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
}
