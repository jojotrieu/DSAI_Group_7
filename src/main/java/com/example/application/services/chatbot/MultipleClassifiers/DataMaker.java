package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.utils.TextFileIO;

import java.io.File;
import java.util.*;

public class DataMaker {
    private static List<String> allPossibleQ = null;
    private static String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/data/";
    private static String P = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/";

    public static void makeDataForUnique(String skill, double split){
        generateAllQ();
        Random r = new Random();

        String currPath = PATH+skill.substring(1, skill.length()-1);
        File dir = new File(currPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String[] traintest= new String[]{"/train", "/test"};
        for(String tt: traintest){
                File dir2 = new File(currPath+tt);
                if(!dir2.exists()) dir2.mkdir();
        }

        List<String> positive = CFG.combos(skill);

        double limit = (1.0-split)*positive.size();

        for (int i = 0; i < Math.max(limit, 1); i++) {
            TextFileIO.write(currPath+traintest[1]+"/"+i+".txt",List.of( positive.remove(r.nextInt(positive.size()))));
        }
        for (int i = 0; i < positive.size(); i++) {
            TextFileIO.write(currPath+traintest[0]+"/"+i+".txt",List.of( positive.get(i)));
        }

    }

    public static void makeAllQnA(){
        List<String> result = new ArrayList<>();
        generateAllQ();
        
        TextFileIO.write(P+"CFG_Q_corpus.txt", allPossibleQ);
    }

    public static void makeData(String skill, double split){
        generateAllQ();
        String currPath = PATH+skill.substring(1, skill.length()-1);
        File dir = new File(currPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String[] traintest= new String[]{"/train", "/test"};
        String[] posneg = new String[]{ "/neg","/pos"};
        for(String tt: traintest){
            for(String pn:posneg){
                File dir2 = new File(currPath+tt);
                if(!dir2.exists()) dir2.mkdir();
                dir2 = new File(currPath+tt+pn);
                if(!dir2.exists()) dir2.mkdir();
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
//        List<String> train_p = new ArrayList<>();
//        List<String> train_n = new ArrayList<>();
//        List<String> test_p = new ArrayList<>();
//        List<String> test_n = new ArrayList<>();

        double limit = 1-positive.size()*split;

        for (int i = 0; i < Math.max(positive.size(), 1); i++) {
            TextFileIO.write(currPath+traintest[1]+posneg[1]+"/"+i+".txt",List.of( positive.remove(r.nextInt(positive.size()))));
            TextFileIO.write(currPath+traintest[1]+posneg[0]+"/"+i+".txt",List.of( negative.remove(r.nextInt(negative.size()))));
        }
        for (int i = 0; i < limit; i++) {
//            train_p.add(positive.remove(r.nextInt(positive.size())));
//            train_n.add(negative.remove(r.nextInt(negative.size())));
            TextFileIO.write(currPath+traintest[0]+posneg[1]+"/"+i+".txt",List.of( positive.get(i)));
            TextFileIO.write(currPath+traintest[0]+posneg[0]+"/"+i+".txt",List.of( negative.get(i)));
        }
//
//        TextFileIO.write(currPath+"/p_train.txt", train_p);
//        TextFileIO.write(currPath+"/p_test.txt", test_p);
//        TextFileIO.write(currPath+"/n_train.txt", train_n);
//        TextFileIO.write(currPath+"/n_test.txt", test_n);
    }

    private static void generateAllQ() {
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
    }

    public static void main(String[] args){
        ChatBot.init();
        for(String skill: CFG.getAllActionRules()){
            makeDataForUnique(skill, 0.8);
        }

    }
}
