package com.example.application.services;

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

        Question question2 = new Question("What class do we have on <DAY> at <TIME>?");


        jsonFile.newSkill(question2);

        jsonFile.addSlot(question2, "Monday");
        jsonFile.addSlot(question2, "Friday");

        JSONObject actionConditions2 = new JSONObject();
        actionConditions2.put("<day>", "Monday");
        actionConditions2.put("<time>", "12pm");

        jsonFile.addAction(question2, "Math", actionConditions2);

        String ans = jsonFile.answer("What do we eat on Monday at 12pm?");
        System.out.println(ans);

    }


}
