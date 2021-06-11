package com.example.application.services.chatbot;

import com.example.application.services.chatbot.spellcheck.SpellCheck;
import com.example.application.services.utils.TextFileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Classifier {
    private static HashMap<String, List<String>> synonymsMap = new HashMap<>();
    private static List<List<String>> allPhrases;
    private static Set<String> allWords = new HashSet<>();
    private static List<String> corpus;
    private static final double THRESHOLD = .75;

    public static void init(){
        allPhrases=CFG.getAllPhrases();
        corpus = TextFileIO.read("src/main/java/com/example/application/services/chatbot/dsai.txt");
        //creates a set of all words in the phrases produced by CFG
        for(List<String> phrase : allPhrases){
            allWords.addAll(phrase);
            corpus.addAll(phrase);
        }
        try {
            File textFile = new File("src/main/java/com/example/application/services/chatbot/synonyms.csv");
            Scanner myReader = new Scanner(textFile);
            int iter = 0;
            while (myReader.hasNextLine() && iter<allWords.size()) {
                String[] data = myReader.nextLine().split("\\W+");
                String word = data[0];
                if(allWords.contains(word)){
                    List<String> synonyms = new ArrayList<>(Arrays.asList(data).subList(2, data.length));
                    if(synonymsMap.containsKey(word)){
                        synonyms.addAll(synonymsMap.get(word));
                    }
                    synonymsMap.put(word,synonyms);
                    iter++;
                }

            }
            myReader.close();
            System.out.println("Synonyms mapped.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        for(List<String> synonyms : synonymsMap.values()){
            corpus.addAll(synonyms);
        }
        SpellCheck.init(corpus);
    }

    public static String process(String query){
        String[] words = query.toLowerCase().split("\\W+");
        //spellcheck each word
        for (int i = 0; i < words.length; i++) {
            words[i] = SpellCheck.suggestSimilarWord(words[i]);
            //converts any synonyms to a main entry
            if(!synonymsMap.containsKey(words[i])){
                for(Map.Entry<String,List<String>> entry : synonymsMap.entrySet()){
                    for(String target : entry.getValue()){
                        if(target.equals(words[i])){
                            words[i]=target;
                            break;
                        }
                    }
                }
            }
        }

        HashMap<String,Double> scores = new HashMap<>();
        for(List<String> phrase: allPhrases){
            double score = 0;
            for(String word : words){
                for(String target : phrase){
                    if(target.equals(word)){
                        score++;
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for(String s : phrase){
                stringBuilder.append(s);
            }
            scores.put(stringBuilder.toString(),score/phrase.size());
        }
        String bestMatch = "";
        double bestScore = 0;
        for(Map.Entry<String,Double> entry : scores.entrySet()){
            double bestDiff = Math.abs(bestScore-1);
            double candidateDiff = Math.abs(entry.getValue()-1);
            if(candidateDiff<bestDiff){
                bestScore=entry.getValue();
                bestMatch=entry.getKey();
            }
        }
        if(bestScore>THRESHOLD){
            return bestMatch;
        } else {
            return "";
        }
    }

}
