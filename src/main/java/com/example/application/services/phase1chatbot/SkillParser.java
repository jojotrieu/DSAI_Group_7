package com.example.application.services.phase1chatbot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class SkillParser {
    private final String path = "src/main/java/com/example/application/services/phase1chatbot/skills.json";
    private JSONObject skillsArray = null;

    public SkillParser() {
        loadSkills();
    }

    public void loadSkills() {
        JSONParser jParser = new JSONParser();

        try (FileReader file = new FileReader(path)) {
            skillsArray = (JSONObject) jParser.parse(file);

        } catch (IOException | ParseException f) {
            f.printStackTrace();
        }
    }

    public void newSkill(Question question) {
        String questionString = question.getSkill();

        if (skillsArray.get(questionString) == null) {
            skillsArray.put(questionString, getSkillTemplate(question));

            updateFile();
        }
    }


    public void addSlot(Question question, String slot) {
        String questionString = question.getSkill();
        JSONObject parameters = (JSONObject) skillsArray.get(questionString);
        JSONArray slots = (JSONArray) parameters.get("slots");

        if (!slots.contains(cleanString(slot))) {
            slots.add(cleanString(slot));
            updateFile();
        }
    }

    public void addAction(Question question, String action, JSONObject conditions) {
        String questionString = question.getSkill();
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
        try (FileWriter file = new FileWriter(path)) {
            file.write(skillsArray.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that answers the query based on the skillsArray
     *
     * @param query question to look for in the skills
     * @return possible answer if query matches with data stored in the answers
     */
    public String answer(String query) {
        loadSkills();
        String result = "I don't know";
        if (query.contains("?")) query = query.substring(0, query.indexOf("?")); // getting rid of the question mark
        for (Object keyset : skillsArray.keySet()) { //iterate through the key of skillsArray
            String skillQuestion = (String) keyset;
            if (correspond(query, skillQuestion)) {
                result = checkSlots(skillQuestion, query);
            }
        }
        return result;
    }

    /**
     * compare the query with the skill to see if the query correspond
     *
     * @param skillQuestion skill coming from the skill array
     * @param query         question asked by user
     * @return possible answer if query matches with data stored in the answers
     */
    private String checkSlots(String skillQuestion, String query) {
        JSONObject skill = (JSONObject) skillsArray.get(skillQuestion);
        JSONObject action = (JSONObject) skill.get("actions");
        for (Object answer : action.keySet()) {
            JSONObject conditions = (JSONObject) action.get(answer);
            String[] queryArray = query.split(" "), questionArray = skillQuestion.split(" ");

            boolean found = checkPlaceHolders(queryArray, questionArray, conditions);

            if (found) return (String) answer;
        }
        return "I don't know";
    }

    /**
     * check if place holders correspond to those of the conditions (of the current answer)
     *
     * @param queryArray    question asked in string array (by user)
     * @param questionArray question in data
     * @param conditions    condition of the current answer
     * @return true if it corresponds
     */
    private boolean checkPlaceHolders(String[] queryArray, String[] questionArray, JSONObject conditions) {
        for (int i = 0; i < queryArray.length; i++) {
            if (questionArray[i].contains("<")) {
                for (Object key : conditions.keySet()) {
                    String placeHolder = (String) key;
                    if (placeHolder.equalsIgnoreCase(questionArray[i]) && !(conditions.get(key)).toString().equalsIgnoreCase(queryArray[i]))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * check if the query correspond the the skill stored in data
     *
     * @param query         question asked by user
     * @param skillQuestion skill from data
     * @return true if it corresponds
     */
    private boolean correspond(String query, String skillQuestion) {
        String[] qarray = query.split(" "),
                sQarray = skillQuestion.split(" ");
        for (int i = 0; i < Math.min(qarray.length,sQarray.length); i++) {
            if (!qarray[i].equalsIgnoreCase(sQarray[i]) && !sQarray[i].contains("<")) return false;
        }
        return true;
    }

    public JSONObject getSkillsArray() {
        return skillsArray;
    }

    /**
     * Delete one skill from the JSON file
     *
     * @param questionToDelete question to delete with placeholders
     *                         the placeholders don't need to correspond
     * @return true if the question was deleted
     */
    public boolean deleteSkill(String questionToDelete){
        loadSkills();
        boolean found = false;
        JSONObject newSkillArray = new JSONObject();
        for (Object keyset : skillsArray.keySet()) { //iterate through the key of skillsArray
            String skillQuestion = (String) keyset;
            if (!correspond(questionToDelete, skillQuestion)||!correspond(skillQuestion,questionToDelete)) {
                newSkillArray.put(keyset, skillsArray.get(keyset));
            }else{
                found = true;
            }
        }

        skillsArray = newSkillArray; //update the skill array
        updateFile();
        System.out.println(skillsArray);
        return found;

    }
}
