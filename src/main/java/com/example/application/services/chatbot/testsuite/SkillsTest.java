package com.example.application.services.chatbot.testsuite;

import com.example.application.services.chatbot.Action;
import com.example.application.services.chatbot.Skills;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class SkillsTest {

    @Test
    void isValidAction() throws FileNotFoundException {
        Action action = new Action();
        action.setVariable("<FORTESTINGPURPOSESONLY>");
        action.getNonTerminals().put("<DAY>","Friday Afternoon");
        action.setExpression("Friday is fun day");
        assert Skills.isValidAction(action);
        action.setVariable("adadsf");
        assert !Skills.isValidAction(action);
        action.setVariable("<FORTESTINGPURPOSESONLY>");
        action.getNonTerminals().put("<DAY>","Friday <AD>");
        assert !Skills.isValidAction(action);
        action.getNonTerminals().put("<DAY>","Friday Afternoon");
        action.setExpression("");
        assert !Skills.isValidAction(action);
        action.setExpression("Friday is fun day <DAY>");
        assert !Skills.isValidAction(action);

    }
}
