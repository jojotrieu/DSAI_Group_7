package com.example.application.services.silichatbot;

import com.example.application.services.ChatBot;
import com.example.application.services.chatbot.*;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class Experiments {
  public static void main(String[] args) throws IOException {

    CFG.loadRules();
    Classifier.init();
    for(String word : Classifier.getAllWords()){
      System.out.println(word);
    }
    System.out.println(Classifier.getAllWords().size());

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
