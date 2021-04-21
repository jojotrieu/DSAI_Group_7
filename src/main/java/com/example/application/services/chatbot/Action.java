package com.example.application.services.chatbot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Action {
    int id;
    String variable;
    @EqualsAndHashCode.Exclude
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

    public Action copy(){
        Action copy = new Action();
        copy.id=id;
        copy.variable=variable;
        copy.nonTerminals=new HashMap<>(nonTerminals);
        copy.expression=expression;
        return copy;
    }
}
