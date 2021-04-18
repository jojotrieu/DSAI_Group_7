package com.example.application.services.chatbot;

import lombok.Data;

import java.util.List;

@Data
public class Action {
    int id;
    String variable;
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
}
