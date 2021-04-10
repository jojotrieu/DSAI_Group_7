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

    public String getVariable() {
        return variable;
    }

    public List<String> getExpressions() {
        return expressions;
    }
}
