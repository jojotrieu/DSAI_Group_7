package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.silichatbot.Corpus2Vec;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.graph.ComputationGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleClassifiers {
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/Models/";
    private static HashMap<String, ComputationGraph> models=null;
    private static double threshhold = 0.80;
    private static Word2Vec w2v = null;


    public static void init(){
        models = new HashMap<>();
        try {
            for (String skill : CFG.getAllActionRules()) {
                models.put(skill, ComputationGraph.load(new File(PATH + skill.substring(1, skill.length() - 1)), true));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        Corpus2Vec corpus2Vec = new Corpus2Vec("corpus.txt");
//        corpus2Vec.cleanCorpus();
//        corpus2Vec.init();
        w2v = corpus2Vec.getWord2Vec();
    }

    public static String predict(String query){
        Map<String, Double> scores = new HashMap<>();
        double max = -1;
        for(Map.Entry<String, ComputationGraph> entry: models.entrySet()){
            double score = entry.getValue().outputSingle(w2v.getWordVectors(new ArrayList<>(List.of(query.split(" "))))).getDouble(1);
            if(score>threshhold){
                scores.put(entry.getKey(), score);
                if(max<score) score = max;
            }
        }
        for(Map.Entry<String, Double> entry: scores.entrySet()) if(max == entry.getValue()) return entry.getKey();
        return null;
    }

}
