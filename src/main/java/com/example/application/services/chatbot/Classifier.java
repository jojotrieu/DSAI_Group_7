package com.example.application.services.chatbot;

import com.example.application.services.chatbot.spellcheck.SpellCheck;
import com.example.application.services.utils.TextFileIO;

import java.util.*;

public class Classifier {
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
        SpellCheck.init(corpus);
    }

    public static String process(String query){
        String[] words = query.toLowerCase().split("\\W+");
        //spellcheck each word
        for (int i = 0; i < words.length; i++) {
            words[i] = SpellCheck.bestMatch(words[i]);
        }
        HashMap<String,Double> scores = new HashMap<>();
        for(List<String> phrase: allPhrases){
            double score = 0;
            for(String word : words){
                for(String target : phrase){
                    if(target.toLowerCase().equals(word)){
                        score++;
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for(String s : phrase){
                stringBuilder.append(s).append(" ");
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
        System.out.println("Classifier bestMatch score: " + bestScore + " | " + bestMatch);
        if(bestScore>THRESHOLD){
            return bestMatch;
        } else {
            return "";
        }
    }

    public static Set<String> getAllWords() {
        return allWords;
    }
}
