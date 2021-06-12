package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.utils.TextFileIO;

import java.util.*;

public class DataMaker {
    private static List<String> allPossibleQ = null;
    private static String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/";
    public static void makeData(String skill, double split){
        if(allPossibleQ==null) {
            allPossibleQ=new ArrayList<>();
            List<List<String>> allPhrasesAsList = CFG.getAllPhrases();
            for(List<String> sentence : allPhrasesAsList){
                StringBuilder s = new StringBuilder();
                for(String w : sentence){
                    s.append(w).append(" ");
                }
                allPossibleQ.add(s.toString().strip());
            }
        }
        List<String> positive = CFG.combos(skill);
        List<String> negative = new ArrayList<>();
        Random r = new Random();
        String neg;
        for (int i = 0; i < positive.size(); i++) {
            do {
               neg = allPossibleQ.get(r.nextInt(allPossibleQ.size()));
            }while(positive.contains(neg));
            negative.add(neg);
        }
        List<String> train = new ArrayList<>();
        List<String> test = new ArrayList<>();
        for (int i = 0; i < positive.size()*split; i++) {
            train.add(positive.remove(r.nextInt(positive.size())));
            train.add(negative.remove(r.nextInt(negative.size())));
        }
        test.addAll(positive);
        test.addAll(negative);
        Collections.shuffle(test);
        TextFileIO.write(PATH+skill.substring(1, skill.length()-1)+"_train.txt", train);
        TextFileIO.write(PATH+skill.substring(1, skill.length()-1)+"_test.txt", test);
    }

    public static void main(String[] args){
        ChatBot.init();
        for(String skill: CFG.getAllActionRules()){
            makeData(skill, 0.8);
        }
    }
}
