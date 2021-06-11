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
            String actionTemp;
            String actionVariable = null;
            List<String> allActionRules = CFG.getAllActionRules();
            if(CNF.splitRules(action).length>2) {
                do {
                    actionTemp = null;
                    String[] allActions = CNF.splitRules(action);
                    action = "";
                    for (int i = 0; i < allActions.length; i++) {
                        if (i < 2) action += allActions[i] + " ";
                        else actionTemp += allActions[i] + " ";
                    }
                    action = transformBack(action);

                    for(Rule r:CFG.getRules()){
                        for(String s : r.getExpressions()){
                            if(s.equals(action)){
                                CYK.setAction(action);
//                                actionVariable=;
                                if(allActionRules.contains(r.getVariable()))
                                    actionVariable= r.getVariable();
                                break;
                            }
                        }
                    }
                    action = actionTemp;
                }while(actionTemp!=null);
            }else {
                action = transformBack(action);
            }

            if( action != null && CNF.splitRules(action).length==1) actionVariable = action;
            else if(actionVariable==null) CYK.setAction(action);
//            System.out.println(action);

            List<String> allaction = CFG.getAllActionRules();
            for (int i = 0; i < 5 && !allaction.contains(actionVariable); i++) {
                for (Rule r : CFG.getRules()) {
                    for (String s : r.getExpressions()) {
                        if (s.equals(action)) {

                            actionVariable = r.getVariable();
                            action = r.getVariable();
                        }
//                        if(actionVariable!=null) break;
                    }
//                    if(actionVariable!=null) break;
                }
            }

            /**
             * this piece of code retrieve the answer
             */
            List<Action> skills = Skills.getActionsCopy();
            Map<String, String> placeHolders = CYK.getPlaceHolders();
//            System.out.println("yooo: "+actionVariable);
//            for(Map.Entry entry:CYK.getPlaceHolders().entrySet()) System.out.println(entry.getKey()+" "+entry.getValue());
            // this part retranscribe multiples words into the placeholders
            for(Map.Entry<String, String> plhld: placeHolders.entrySet()){
                while(plhld.getValue().contains(CNF.getPREFIX())){
                    String[] var = CNF.splitRules(plhld.getValue());
                    String newValue= "";
                    for(String s : var){
                        if(s.contains(CNF.getPREFIX())){
                            newValue+= CNF.getCnf().get(s).get(0)+" ";
                        }else{
                            newValue+=s+" ";
                        }
                    }
                    plhld.setValue(newValue.strip());
                }
            }
            for (Action a : skills) {
                if (a.getVariable().equals(actionVariable)) {
                    Map<String,String> nonTerminals = a.getNonTerminals();
                    String mapToString = a.getNonTerminalsToString();
                    boolean found = true;
                    for(Map.Entry<String,String> e : nonTerminals.entrySet()){
                        if(placeHolders.containsKey(e.getKey())
                                && !placeHolders.get(e.getKey()).equals(e.getValue())){
                            found = false;
                        }
                    }if(found||(mapToString.indexOf(' ')+1<mapToString.length() && placeHolders.containsValue(mapToString.substring(mapToString.indexOf(' ')+1)))) return a.getExpression();
                }
            }
        } return "I don't know";
    }

    private static String transformBack(String action) {
        while (action.contains(PREFIX)) { // while some of the placeholder are made up by CNF new variables
            String[] arguments = CNF.splitRules(action);
            String nAction = "";
            for (int i = 0; i < arguments.length; i++) {
//                    System.out.println("WTF" + CNF.getCnf().get("<zzzplhldY206>").get(0));
                if (arguments[i].contains(PREFIX)) {
//                        System.out.println("WTF" + CNF.getCnf().get("<zzzplhldY206>").get(0) + "   "+ arguments[i]);
                    nAction += CNF.getCnf().get(arguments[i]).get(0); // replace those with
                } else {
                    nAction += arguments[i];
                }
                if (i < arguments.length - 1) nAction += " ";
            }
            action = nAction;
        }
        return action;
    }
}
