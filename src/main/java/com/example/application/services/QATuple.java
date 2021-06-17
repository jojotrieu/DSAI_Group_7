package com.example.application.services;

public class QATuple {
    private final String question;
    private final String answer;
    private double evaluation;
    public QATuple(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public double getEvaluation() {
        return this.evaluation;
    }
}
