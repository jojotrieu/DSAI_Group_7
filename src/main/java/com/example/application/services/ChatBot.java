package com.example.application.services;

import com.example.application.services.chatbot.*;

import java.util.List;
import java.util.Map;

public class ChatBot {
    private static final String PREFIX = CNF.getPREFIX();
    
    public static void init(){
        CFG.loadRules();
        Skills.loadActions();
        CNF.initialize();
    }

    public static String respondTo(String question) {
        if (CYK.isValidLanguage(question)) {
            String action = CYK.getAction();
            while (action.contains(PREFIX)) { // while some of the placeholder are made up by CNF new variables
                String[] arguments = action.split(" ");
                String nAction = "";
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i].contains(PREFIX)) {
                        nAction += CNF.getCnf().get(arguments[i]).get(0); // replace those with
                    } else {
                        nAction += arguments[i];
                    }
                    if (i < arguments.length - 1) nAction += " ";
                }
                action = nAction;
                CYK.setAction(action);
            }
//            System.out.println(action);
            String actionVariable = null;

            CFG.loadRules();
            for (Rule r : CFG.getRules()) {
                for (String s : r.getExpressions()) {
                    if (s.equals(action)) actionVariable = r.getVariable();
                }
            }
            /**
             * this piece of code retrieve the answer
             */
            List<Action> skills = Skills.getActionsCopy();
            Map<String, String> placeHolders = CYK.getPlaceHolders();
//            System.out.println("yooo: "+actionVariable);
//            for(Map.Entry entry:CYK.getPlaceHolders().entrySet()) System.out.println(entry.getKey()+" "+entry.getValue());
            for (Action a : skills) {
                if (a.getVariable().equals(actionVariable)) {
                    Map<String,String> nonTerminals = a.getNonTerminals();
                    boolean found = true;
                    for(Map.Entry<String,String> e : nonTerminals.entrySet()){
                        if(placeHolders.containsKey(e.getKey())
                                && !placeHolders.get(e.getKey()).equals(e.getValue())){
                            found = false;
                        }
                    }if(found) return a.getExpression();
                }
            }
        } return "I don't know";
    }
}
