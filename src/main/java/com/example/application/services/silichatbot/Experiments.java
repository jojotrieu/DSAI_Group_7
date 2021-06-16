package com.example.application.services.silichatbot;

import com.example.application.services.chatbot.*;
import com.example.application.services.utils.TextFileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Experiments {
  public static void main(String[] args) throws IOException {

    CFG.loadRules();

    List<String> rule = CFG.getAllActionRules();
    for(String phrase: rule){
      System.out.println(phrase);
    }


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
