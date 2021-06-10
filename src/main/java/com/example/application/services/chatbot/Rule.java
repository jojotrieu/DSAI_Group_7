package com.example.application.services.chatbot;

import com.jpattern.jobexecutor.console.ExecutorWrappedList;
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
