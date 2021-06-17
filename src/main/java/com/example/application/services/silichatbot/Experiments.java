package com.example.application.services.silichatbot;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.rnnclassifier.RNNClassifier;
import com.example.application.services.chatbot.spellcheckML.Rephraser;

import java.io.IOException;

public class Experiments {
  public static void main(String[] args) throws IOException, InterruptedException {

    CFG.loadRules();
    Rephraser.fill(15);
    Rephraser.writeSeq2Disk();
    RNNClassifier.init();


    /*
    Corpus2Vec corpus2Vec = new Corpus2Vec("corpus.txt");
    corpus2Vec.cleanCorpus();
    corpus2Vec.init();
    Word2Vec word2Vec = corpus2Vec.getWord2Vec();
    word2Vec.getWordVector("class");
    Collection<String> lst = word2Vec.wordsNearest("class", 10);
    lst.forEach(System.out::println);


     */
  }
}
