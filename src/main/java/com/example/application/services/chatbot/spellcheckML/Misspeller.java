package com.example.application.services.chatbot.spellcheckML;

import java.util.*;

public class Misspeller {
    private static final HashMap<String,List<String>> misspellings = new HashMap<>();
    private static final List<Character> alphabet =
            Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

    public static void fill (Set<String> allWords, int amount, int noise){
        misspellings.clear();
        for(String word : allWords){
            Set<String> misspelling = new HashSet<>();
            for (int i = 0; i < amount; i++) {
                StringBuilder str = new StringBuilder(word.toLowerCase());
                for (int j = 0; j < noise; j++) {
                    mutate(str);
                }
                if(str.toString().equals(word)){
                    i--;
                } else {
                    if(!misspelling.add(str.toString())){
                        i--;
                    }
                }
            }
            misspellings.put(word.toLowerCase(),new ArrayList<>(misspelling));
        }
    }

    public static String mutate(StringBuilder str){
        int rand = (int) (Math.random()*5d);
        switch (rand){
            case 1: deletion(str); break;
            case 2: inversion(str); break;
            case 3: insertion(str); break;
            case 4: duplication(str); break;
        }
        return str.toString();
    }

    private static void deletion(StringBuilder word){
        if(word.length()>2){
            int rand = (int) (Math.random()* (double) word.length());
            word.deleteCharAt(rand);
        }
    }

    private static void insertion (StringBuilder word){
        int rand = (int) (Math.random()* (double) word.length());
        word.insert(rand,randomLetter());
    }

    private static void duplication(StringBuilder word){
        int rand = (int) (Math.random()* (double) word.length());
        word.insert(rand,word.charAt(rand));
    }

    private static void inversion(StringBuilder word){
        if(word.length()>1){
            int rand = (int) (Math.random()* (double) (word.length()-1));
            StringBuilder section = new StringBuilder(word.substring(rand,rand+2));
            word.delete(rand,rand+2);
            word.insert(rand,section.reverse());
        }
    }

    private static char randomLetter(){
        int rand = (int) (Math.random()*26d);
        return alphabet.get(rand);
    }

    public static HashMap<String, List<String>> getMisspellings() {
        return misspellings;
    }
}
