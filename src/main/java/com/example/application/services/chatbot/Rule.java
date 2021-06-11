package com.example.application.services.chatbot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Data
public class Rule {
    int id;
    String variable;
    @EqualsAndHashCode.Exclude
    List<String> expressions = new ArrayList<>();
    @EqualsAndHashCode.Exclude
    List<List<String>> splitExpressions = new ArrayList<>();

    public Rule copy(){
        Rule copy = new Rule();
        copy.setId(id);
        copy.setVariable(variable);
        copy.setExpressions(new ArrayList<>(expressions));
        return copy;
    }

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

    public static String expressionToString(List<String> expressions) {
        String expr = expressions.toString();
        expr = expr.substring(1, expr.length() - 1);
        return expr;
    }

    public static List<String> expressionToArray(String value) {
        String[] array = value.split(",", 20);
        return new ArrayList<>(Arrays.asList(array));
    }
}
