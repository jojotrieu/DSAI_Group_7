package com.example.application.services;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

    public void newSkill(String question) {
        if (skillsArray.get(question) == null) {
            skillsArray.put(question, getSkillTemplate());

            updateFile();
        }
    }

    public void addSlot(String question, String slot) {
        JSONObject parameters = (JSONObject) skillsArray.get(question);
        JSONArray slots = (JSONArray) parameters.get("slots");

        if (!slots.contains(slot)) {
            slots.add(slot);
            updateFile();
        }
    }

    public void addAction(String question, String action, JSONObject conditions) {
        JSONObject parameters = (JSONObject) skillsArray.get(question);
        JSONObject actions = (JSONObject) parameters.get("actions");

        actions.put(action, conditions);
        updateFile();
    }

    private JSONObject getSkillTemplate() {
        JSONObject skillTemplate = new JSONObject();
        skillTemplate.put("slots", new JSONArray());
        skillTemplate.put("actions", new JSONObject());

        return skillTemplate;
    }

    private void updateFile() {
        try (FileWriter file = new FileWriter(path)){
            file.write(skillsArray.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
