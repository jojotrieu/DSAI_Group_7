package com.example.application.services.silichatbot;

import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.IOException;
import java.util.Collection;

public class Experiments {
  public static void main(String[] args) throws IOException {
    Corpus2Vec corpus2Vec = new Corpus2Vec();
    corpus2Vec.cleanCorpus();
    corpus2Vec.init();
    Word2Vec word2Vec = corpus2Vec.getWord2Vec();
    Collection<String> lst = word2Vec.wordsNearest("student", 10);
    lst.forEach(System.out::println);
  }
}
