package com.example.application.services.chatbot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EXAMPLE: <SCHEDULE> * <TIME> 12 <DAY> Monday * On Monday noon we have Theoretical Computer Science
 *
 * <SCHEDULE> -> variable
 * "*" denotes start and end of non-terminals
 * <TIME> -> added to Map nonTerminals as a key
 * "12" -> added to Map nonTerminals as value to key <TIME>
 * <DAY> -> added to Map nonTerminals as a key
 * "Monday" -> added to Map nonTerminals as value to key <DAY>
 * "On Monday noon we have Theoretical Computer Science" -> expression
 */

@Data
public class Action {
    int id;
    String variable;
    @EqualsAndHashCode.Exclude
    Map<String,String> nonTerminals = new HashMap<>();
    String expression;

    public void addElement2NonTerminals(String key, String value) {
        this.nonTerminals.put(key, value);
    }

    public String getNonTerminalsToString(){

        if(!nonTerminals.keySet().isEmpty()){
            StringBuilder mapAsString = new StringBuilder();
            for (String key : nonTerminals.keySet()) {
                mapAsString.append(key + " " + nonTerminals.get(key) + " ");
            }
            mapAsString.delete(mapAsString.length()-1, mapAsString.length());
            return mapAsString.toString();
        }else{
            String empty = "";
            return empty;
        }
    }

    public static Map<String, String> stringToHashMap(String nt){
        Map<String, String> map = new HashMap<>();

        if(nt.isEmpty()){
            return map; // return empty map
        }else{
            String[] array = nt.split(" ", 20);

            for(int i = 0; i < array.length; i +=2){
                if(i+1<array.length && i<array.length){
                    map.put(array[i], array[i+1]);
                }else if (i<array.length && i+1>=array.length){
                    map.put(array[i], "NULL");
                }
            }
            return map;
        }
    }

    public Action copy(){
        Action copy = new Action();
        copy.id=id;
        copy.variable=variable;
        copy.nonTerminals=new HashMap<>(nonTerminals);
        copy.expression=expression;
        return copy;
    }
}
