package com.example.application.services.chatbot;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Action {
    int id;
    String variable;
    Map<String,String> nonTerminals = new HashMap<>();
    String expression;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String var) {
        this.variable = var;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expr) {
        this.expression = expr;
    }

    public Map<String, String> getNonTerminals() {
        return nonTerminals;
    }

}
