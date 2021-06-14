package com.example.application.services.chatbot.MultipleClassifiers;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.silichatbot.Corpus2Vec;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MultipleClassifiers {
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/Models/";
    private static HashMap<String, ComputationGraph> models=null;
    private static final double threshold = 0.80;
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
            INDArray value = w2v.getWordVectors(new ArrayList<>(List.of(query.split(" "))));
            if(value.shape().length>0) {
                double score = entry.getValue().outputSingle(value.reshape(1, 1, value.shape()[0], 200)).getDouble(1);
                if (score > threshold) {
                    scores.put(entry.getKey(), score);
                    if (max < score) max = score;
                }
            }
        }
        for(Map.Entry<String, Double> entry: scores.entrySet()) if(max == entry.getValue()) return entry.getKey();
        return null;
    }

    public static void main(String[] args){
        ChatBot.init();
        init();
        String path = "src/main/java/com/example/application/services/chatbot/MultipleClassifiers/data2/";

        StringBuilder result = new StringBuilder();
        int good = 0, bad = 0;
        int unpredicted = 0;
        try {
            for (Map.Entry<String, ComputationGraph> entry : models.entrySet()) {
                int correct=0, wrong = 0;
                String skill = entry.getKey();
                String filePathNeg = path + skill.substring(1, skill.length() - 1) + "/test/neg/0.txt";
                String filePathPos = path + skill.substring(1, skill.length() - 1) + "/test/pos/0.txt";
                String neg = FileUtils.readFileToString(new File(filePathNeg), "UTF-8");
                String pos = FileUtils.readFileToString(new File(filePathPos), "UTF-8");
                System.out.println(neg);
                System.out.println(pos);
                INDArray posVal = w2v.getWordVectors(List.of(CNF.splitRules(pos)));
                System.out.println(posVal);
                INDArray negVal =w2v.getWordVectors(List.of(CNF.splitRules(neg)));
                if(posVal.shape().length==0){
                    wrong+=1; unpredicted+=1;
                }else {
                    INDArray predPos = entry.getValue().outputSingle(posVal.reshape(1, 1, posVal.shape()[0], 200));

                    if (predPos.getDouble(1) > threshold) correct += 1;
                    else wrong += 1;
                }
                if(negVal.shape().length==0){
                    wrong +=1; unpredicted+=1;
                }else{
                    INDArray predNeg = entry.getValue().outputSingle(negVal.reshape(1, 1, negVal.shape()[0], 200));
                    if (predNeg.getDouble(0) > threshold) correct += 1;
                    else wrong += 1;
                }
                good += correct;
                bad += wrong;
                result.append("For skill ").append(skill).append(": ").append(correct).append(" correct / ").append(wrong).append(" wrong\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(result);
        System.out.println("total correct: "+good+"\ntotal wrong: "+bad);
        System.out.println("with "+ unpredicted+" instances not predicted in the wrong");
    }

}
