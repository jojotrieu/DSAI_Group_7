package com.example.application.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuestionHandler {
    private File file;
    private Scanner scanner;
    private ArrayList<QATuple> tuples;
    private double threshold = 0.3;
    /**
     * Creates a question handler.
     * The specified path is the path to the question-answer database.
     * <pre>{@code
     *  QuestionHandler handler = new QuestionHandler("../myDataBasePath");
     * }</pre>
     * @param filePath A path to the question-answer database text file.
     */
    public QuestionHandler(String filePath) {
        this.file = new File(filePath);
        this.tuples = new ArrayList<QATuple>();
        try {
            this.scanner = new Scanner(this.file);
            ArrayList<String> lines = new ArrayList<String>();
            while(this.scanner.hasNextLine()) {
                String line = this.scanner.nextLine();
                lines.add(line);
                System.out.println(line);
            }
            System.out.println("========");
            for (String line : lines) {
                //String[] arr = line.split("\\|");
                /*
                for (String str: arr) {
                    System.out.print(str);
                }*/
                QATuple tuple = new QATuple(line.split("\\|")[0], line.split("\\|")[1]);
                this.tuples.add(tuple);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Answers a question asked by the user.
     * treshold's value by default is set at 0.3
     * <pre>
     * * {@code
     * * QuestionHandler handler = new QuestionHandler("MyPath");
     * * handler.answer("What's the distance between DSAI and Maastricht's train station?", 0.6)
     * // Will output the question with the highest similarity evaluation or
     * will respond by a pre defined string in case of low values.
     * }
     * * </pre>
     * @param question a question given by the user.
     * @param threshold A value for which method outputs a predefined string meaning that no questions in the database had a sufficient similarity ratio.
     * @return An answer to the most similar question in the database and  if < treshold returns a predefined string.
     */
    public String answer(String question, double threshold) {
        QATuple bestEvaluation = this.tuples.get(0);
        bestEvaluation.setEvaluation(evaluate(question, bestEvaluation.getQuestion()));
        for(QATuple tuple : this.tuples) {
            tuple.setEvaluation(evaluate(question, tuple.getQuestion()));
            if(tuple.getEvaluation() > bestEvaluation.getEvaluation()){
                bestEvaluation = tuple;
            }
        }
        if(bestEvaluation.getEvaluation() < threshold){
            return failureRandomAnswer();
        }else{
            return bestEvaluation.getAnswer();
        }
    }

    /**
     * Answers a question asked by the user.
     * threshold value by default is set at 0.3
     * <pre>
     * * {@code
     * * QuestionHandler handler = new QuestionHandler("MyPath");
     * * handler.answer("What's the distance between DSAI and Maastricht's train station?")
     * // Will output the question with the highest similarity evaluation or
     * will respond by a pre defined string in case of low values.
     * }
     * * </pre>
     * @param question a question given by the user.
     * @return An answer to the most similar question in the database and  if < treshold returns a predefined string.
     */
    public String answer(String question) {
        QATuple bestEvaluation = this.tuples.get(0);
        bestEvaluation.setEvaluation(evaluate(question, bestEvaluation.getQuestion()));
        for(QATuple tuple : this.tuples) {
            tuple.setEvaluation(evaluate(question, tuple.getQuestion()));
            if(tuple.getEvaluation() > bestEvaluation.getEvaluation()){
                bestEvaluation = tuple;
            }
        }
        if(bestEvaluation.getEvaluation() < threshold){
            return failureRandomAnswer();
        }else{
            return bestEvaluation.getAnswer();
        }
    }

    private int wordsInCommon(String str1, String str2) {
        String[] arr1 = str1.split(" ");
        String[] arr2 = str2.split(" ");
        int count = 0;
        for(int i = 0; i < arr1.length; i++) {
            for(int j = 0; j < arr2.length; j++) {
                if(arr2[j].equals(arr1[i])){
                    count++;
                }
            }
        }
        return count;
    }

    private int wordsAtSameIndex(String str1, String str2) {
        String[] arr1 = str1.split(" ");
        String[] arr2 = str2.split(" ");
        int count = 0;
        for(int i = 0; i < arr1.length; i++) {
            if(i < arr2.length){
                if(arr1[i].equals(arr2[i])){
                    count++;
                }
            }
        }
        return count;
    }

    private double evaluate (String str1, String str2) {
        return (wordsAtSameIndex(str1, str2) + wordsInCommon(str1, str2))/(str1.length()*1.0);
    }
    private void test(){
        for (QATuple tuple: this.tuples) {
            System.out.println(tuple.getAnswer());
        }
    }

    private String failureRandomAnswer() {
        //TODO replace this conditional with a text file.
        double rand = Math.random();
        if(rand < 0.5){
            return "Sorry I did not quite catch what you said.";
        }else {
            return "Sorry I do not understand. Could you repeat please?";
        }
    }
}