package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CYK;
import com.example.application.services.utils.TextFileIO;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
        boolean oneOfTwo = false; // increment only once every two time we find duplicate
        for (int i = 0; i < Math.max(limit, 1); i++) {
            String currWord = positive.remove(r.nextInt(positive.size()));
            TextFileIO.write(currPath+traintest[1]+"/"+i+".txt",List.of(currWord));
            while(positive.contains(currWord)) {
                positive.remove(currWord);
                if(oneOfTwo){
                    oneOfTwo = false;
                    i++;
                }else oneOfTwo = true;
            }
        }
        List<String> woah = new ArrayList<>();
        for (int i = 0; i < positive.size(); i++) {
            String currWord = positive.get(i);
            if(!woah.contains(currWord))
            TextFileIO.write(currPath+traintest[0]+"/"+i+".txt",List.of( currWord));
            woah.add(currWord);
        }

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
    public static void makeAllQnA(){
        List<String> result = new ArrayList<>();
        generateAllQ();
        
        TextFileIO.write(P+"CFG_Q_corpus.txt", allPossibleQ);
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

    public static void verifyData(String skill){
        String currentPath = PATH+ skill.substring(1,skill.length()-1)+"/";
        List<String> test = new ArrayList<>();
        List<String> train = new ArrayList<>();
        File testFile= new File(currentPath+"test/");
        File trainFile = new File(currentPath+"train/");
        try {
            for (File f : testFile.listFiles()) {
                test.add(FileUtils.readFileToString(f));
            }
            for (File f : trainFile.listFiles()) {
                train.add(FileUtils.readFileToString(f));
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        for(String a:train){
            for(String b:test){
//                System.out.println(b);
                if(a.equals(b)) System.out.println(a+"\n"+b);
            }
        }
    }

    public static void createBenchMarkData(){
        String pathSource = PATH;
        String pathDest = "src/main/java/com/example/application/services/chatbot/DataBenchMark/";
        try {
            int countSkill = 0, countS=0;
            ArrayList<String> phrases = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            for (String skill : CFG.getAllActionRules()) {
                String s = skill.substring(1, skill.length() - 1);
                File f = new File(pathSource + s + "/unknown");
                countSkill++;
                for (File mf : f.listFiles()) {
                    String curr = FileUtils.readFileToString(mf, "UTF-8").stripTrailing();
                    phrases.add(curr);
                    labels.add(skill);
                    countS++;
                }

//                TextFileIO.write(pathDest+s+".txt", text );

            }
            TextFileIO.write(pathDest+"phrases.txt", phrases );
            TextFileIO.write(pathDest+"labels.txt", labels );
            System.out.println("skill:"+countSkill+" sentences:"+ countS);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
      /*  ChatBot.init();
        for(String skill: CFG.getAllActionRules()){
            makeDataForUnique(skill, 0.8);
        }
//        makeAllQnA();

        for(String skill: CFG.getAllActionRules()){
            verifyData(skill);
        }

       */
        ChatBot.init();
        createBenchMarkData();
    }
}
