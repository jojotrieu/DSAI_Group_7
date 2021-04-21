package com.example.application.services.chatbot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
public class Rule {
    int id;
    String variable;
    @EqualsAndHashCode.Exclude List<String> expressions = new ArrayList<>();

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

    public List<String> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<String> expr) {
        this.expressions = expr;
    }

    public Rule copy(){
        Rule copy = new Rule();
        copy.setId(id);
        copy.setVariable(variable);
        copy.setExpressions(new ArrayList<>(expressions));
        return copy;
    }
}
