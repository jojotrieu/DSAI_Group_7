import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SkillParser {
    private JSONParser jParser;
    private JSONArray skillsArray;

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
        try {
            jParser.parse(jsArray.toJSONString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Object action = jsArray.get(0);
        Object entity = jsArray.get(1);
        Object property = jsArray.get(2);
        Skill skill = new Skill((String)action, (String)entity, (String)property);
        return null;
    }

    public void makeSkill(String query){
        if(skillsArray==null) loadFile("skills.JSON");

        JSONObject newSkill = new JSONObject();
        JSONObject contentOfSkill = new JSONObject();

        String firstLine = query.substring(0, query.indexOf("\n")); //TODO: check if newline is \n or \r or something else

        System.out.println(firstLine);
        //TODO: insert each line in a JSON object
        JSONObject jsonObject = new JSONObject();

        newSkill.put(firstLine, contentOfSkill);
        for (int j = 0; j < newSkill.size(); j++) {
            jsonObject.put(newSkill.values(), newSkill.keySet());
        }

        skillsArray.add(newSkill);


    }


    public String[] splitSkill(String skill) {

        return skill.split(" ");
    }

}