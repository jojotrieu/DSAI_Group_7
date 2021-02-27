package com.example.application.services.phase1chatbot;

import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question {
    private String question = "";

    public Question(String question) {
        this.question = question;
        cleanQuestion();
    }

    public String getString() {
        return question;
    }

    private void cleanQuestion() {
        question = question.replace("?", " ?");
        question = question.replace("!", " !");
        question = question.toLowerCase();
        question = question.substring(0, 1).toUpperCase() + question.substring(1);
    }


    public JSONObject getPropertyIndicesTemplate() {
        Pattern p = Pattern.compile("\\<(.*?)\\>");
        Matcher m = p.matcher(question);

        String[] splittedQuestion = question.split(" ");

        JSONObject properties = new JSONObject();

        while (m.find()) {
            String string = m.group(1);
            String property = "<" + string + ">";
            int propertyIndex = ArrayUtils.indexOf(splittedQuestion, property);

            properties.put(property, propertyIndex);
        }

        return properties;
    }
}
