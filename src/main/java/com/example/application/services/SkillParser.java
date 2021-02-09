
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
}
