package com.example.application.services;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SkillParser {
    private JSONParser jParser;
    private JSONArray skillsArray=null;

    public SkillParser(){
        jParser = new JSONParser();

    }

    public void loadFile(String path){
        try (FileReader fr = new FileReader(path)) {
            Object obj = jParser.parse(fr);

            skillsArray = (JSONArray) obj;


        }catch(FileNotFoundException f){
            f.printStackTrace();
        }catch(IOException io){
            io.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String query(String query){
        loadFile("skills.JSON");
        for(Object o: skillsArray){
            JSONArray jsArray = (JSONArray) o;
            if(jsArray.toString().contains(query)){ //TODO: verify if it's the right way to do it

                String result = "";

                Skill skill = makeSkill(jsArray);

                return skill.getAction(); // ??
            }
        }
        return "";
    }

    private Skill makeSkill(JSONArray jsArray) {
        //TODO: make the skill object
        return null;
    }

    public void makeSkill(String query){
        if(skillsArray==null) loadFile("skills.JSON");

        JSONObject newSkill = new JSONObject();
        JSONObject contentOfSkill = new JSONObject();

        String firstLine = query.substring(0, query.indexOf("\n")); //TODO: check if newline is \n or \r or something else

        //TODO: insert each line in a JSON object


        newSkill.put(firstLine, contentOfSkill);

        skillsArray.add(newSkill);


    }


    public String[] splitSkill(String skill) {

        return skill.split(" ");
    }

}
