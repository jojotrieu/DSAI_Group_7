package com.example.application.services.wdclabs;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Corpus2Vec {

    private Word2Vec word2Vec;
    private String modelFileName = "src/main/java/com/example/application/services/wdclabs/word2vecmodel.gz";

    public void init() throws IOException {
        String corpusFileName = "src/main/java/com/example/application/services/wdclabs/cleancorpus.txt";
        SentenceIterator iter = new LineSentenceIterator(new File(corpusFileName));
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        System.out.println("Building Word2Vec model...");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        System.out.println("Fitting Word2Vec model....");
        vec.fit();
        WordVectorSerializer.writeWord2VecModel(vec,modelFileName);
    }

    private void readModel (){
        if(word2Vec==null){
            word2Vec = WordVectorSerializer.readWord2VecModel(modelFileName);
        }
    }

    public void cleanCorpus(){
        List<String> corpus = new ArrayList<>();
        try {
            File textFile =
                    new File("src/main/java/com/example/application/services/wdclabs/rawcorpus.txt");
            Scanner myReader = new Scanner(textFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String [] arr = data.split(",?\\ ");
                for(String s : arr){
                    boolean newLine = false;
                    s = s.toLowerCase();
                    if(!s.isEmpty() && (Character.isDigit(s.charAt(0))||Character.isDigit(s.charAt(s.length()-1)))){
                        s = "<NUM>";
                        corpus.add(s);
                        break;
                    }
                    while(!s.isEmpty() && !Character.isLetter(s.charAt(0))){
                        s = s.substring(1);
                    }
                    while(!s.isEmpty() && !Character.isLetter(s.charAt(s.length()-1))){
                        char lastChar = s.charAt(s.length()-1);
                        if((lastChar=='.' || lastChar=='?' || lastChar=='!')
                                && !s.equals("dr.") && !s.equals("mr.") && !s.equals("ms.")){
                            newLine=true;
                        }
                        if(s.length()>=2 && !Character.isLetter(s.charAt(s.length()-2))){
                            s = s.substring(0,s.length()-1);
                        } else {
                            break;
                        }
                    }
                    if(s.startsWith("http") || s.startsWith("www.")){
                        s="<WEBSITE>";
                    }
                    if(s.indexOf('@')>=0 && s.indexOf('.')>=0){
                        s="<EMAIL_ADDRESS>";
                    }
                    if(!s.isEmpty()){
                        corpus.add(s);
                        if(newLine){
                            corpus.add("[<<NEWLINE>>]");
                        }
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            String cleanFileName = "src/main/java/com/example/application/services/wdclabs/cleancorpus.txt";
            FileWriter myWriter =
                    new FileWriter(cleanFileName, false);
            for(String str : corpus){
                if(str.equals("[<<NEWLINE>>]")){
                    myWriter.write(System.lineSeparator());
                } else {
                    myWriter.write(str + " ");
                }

            }
            myWriter.close();
            System.out.println("Successfully cleaned corpus");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public Word2Vec getWord2Vec() {
        readModel();
        return word2Vec;
    }
}
