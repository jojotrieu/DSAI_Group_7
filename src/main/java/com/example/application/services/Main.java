
import org.json.simple.JSONObject;

public class Main {

    public static void main(String[] args) {

        SkillParser jsonFile = new SkillParser();

        Question question = new Question("What do we eat on <DAY> at <TIME>?");


        jsonFile.newSkill(question);

        jsonFile.addSlot(question, "Monday");
        jsonFile.addSlot(question, "Friday");

        JSONObject actionConditions = new JSONObject();
        actionConditions.put("<day>", "Monday");
        actionConditions.put("<time>", "12pm");

        jsonFile.addAction(question, "Pizza", actionConditions);



    }


}
