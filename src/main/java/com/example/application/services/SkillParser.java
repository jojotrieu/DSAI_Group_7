package com.example.application.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SkillParser {
    private JSONObject skillsArray = null;
    private final String path = "skills.json";

    public SkillParser(){
        loadSkills();
    }

    public void loadSkills(){
        JSONParser jParser = new JSONParser();

        try (FileReader file = new FileReader(path)) {
            skillsArray = (JSONObject) jParser.parse(file);

        } catch(IOException | ParseException f) {
            f.printStackTrace();
        }
    }

    public void newSkill(Question question) {
        String questionString = question.getString();

        if (skillsArray.get(questionString) == null) {
            skillsArray.put(questionString, getSkillTemplate(question));

            updateFile();
        }
    }


    public void addSlot(Question question, String slot) {
        String questionString = question.getString();
        JSONObject parameters = (JSONObject) skillsArray.get(questionString);
        JSONArray slots = (JSONArray) parameters.get("slots");

        if (!slots.contains(cleanString(slot))) {
            slots.add(cleanString(slot));
            updateFile();
        }
    }

    public void addAction(Question question, String action, JSONObject conditions) {
        String questionString = question.getString();
        JSONObject parameters = (JSONObject) skillsArray.get(questionString);
        JSONObject actions = (JSONObject) parameters.get("actions");


        actions.put(cleanString(action), conditions);
        updateFile();
    }

    private String cleanString(String string) {
        String cleanString = string.replace("?", " ?");
        cleanString = cleanString.replace("!", " !");
        cleanString = cleanString.toLowerCase();

        return cleanString.substring(0, 1).toUpperCase() + cleanString.substring(1);
    }

    private JSONObject getSkillTemplate(Question question) {
        JSONObject skillTemplate = new JSONObject();
        skillTemplate.put("slots", new JSONArray());
        skillTemplate.put("actions", new JSONObject());
        skillTemplate.put("property template", question.getPropertyIndicesTemplate());

        return skillTemplate;
    }

    private void updateFile() {
        try (FileWriter file = new FileWriter(path)){
            file.write(skillsArray.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String answer(String query){
        String result = "I don't know";
        if(query.contains("?")) query = query.substring(0,query.indexOf("?")); // getting rid of the question mark
        for(Object keyset : skillsArray.keySet()){ //iterate through the key of skillsArray
            String skillQuestion = (String) keyset;
            if(correspond(query,skillQuestion)){
                JSONObject skill =(JSONObject) skillsArray.get(skillQuestion);
                JSONObject action = (JSONObject) skill.get("actions");
                for(Object answer : action.keySet()){
                    JSONObject conditions = (JSONObject) action.get(answer);

                    String[] queryArray = query.split(" ");
                    String[] questionArray = skillQuestion.split(" ");

//                    System.out.println(conditions);
                    boolean found = true;
                    for (int i = 0; i < queryArray.length; i++) {
                        if(questionArray[i].contains("<") ) {
//                            System.out.println(questionArray[i]);
//                            System.out.println(conditions.get(queryArray[i])); //we can't access the value with a key that is the same string
                            for(Object key: conditions.keySet()){
                                String placeHolder = (String) key;
                                if(placeHolder.equals(questionArray[i])&&!(conditions.get(key)).toString().equals(queryArray[i])) found= false;
                            }

                            if (found) return answer.toString();
                        }
                    }
                    if (found)return (String) answer;
                }
            }
        }
        return result;
    }

    private boolean correspond(String query, String skillQuestion){
        String[] qarray = query.split(" "),
        sQarray= skillQuestion.split(" ");
        for (int i = 0; i < qarray.length; i++) {
            if(!qarray[i].equals(sQarray[i]) && !sQarray[i].contains("<")) return false;
        }
        return true;
    }
}
