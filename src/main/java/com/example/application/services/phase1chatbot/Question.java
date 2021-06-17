package com.example.application.services.phase1chatbot;

import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question {
    private String skill;
    private final List<String> propertiesList = new ArrayList<>();

    public Question(String question,boolean reconstruct) {
        this.skill = question;
        if(!reconstruct){
            cleanQuestion();
        }
        computePropertiesList();
    }

    public String getSkill() {
        return skill;
    }

    private void cleanQuestion() {
        skill = skill.replace("?", " ?");
        skill = skill.replace("!", " !");
        skill = skill.toLowerCase();
        skill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
    }


    public JSONObject getPropertyIndicesTemplate() {
        Pattern p = Pattern.compile("\\<(.*?)\\>");
        Matcher m = p.matcher(skill);

        String[] splittedQuestion = skill.split(" ");
        JSONObject properties = new JSONObject();

        while (m.find()) {
            String string = m.group(1);
            String property = "<" + string + ">";
            int propertyIndex = ArrayUtils.indexOf(splittedQuestion, property);
            properties.put(property, propertyIndex);
        }
        return properties;
    }

    public void computePropertiesList(){
        Pattern p = Pattern.compile("\\<(.*?)\\>");
        Matcher m = p.matcher(skill);
        while (m.find()) {
            String string = m.group(1);
            String property = "<" + string + ">";
            this.propertiesList.add(property);
        }
    }

    public List<String> getPropertiesList() {
        return propertiesList;
    }

    @Override
    public String toString() {
        return skill;
    }
}
