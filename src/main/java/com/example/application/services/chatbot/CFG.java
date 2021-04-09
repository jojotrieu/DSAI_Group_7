package com.example.application.services.chatbot;

import com.example.application.services.utils.TextFileIO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CFG {
    private final List<Rule> rules = new ArrayList<>();
    private String PATH = "src/main/java/com/example/application/services/chatbot/rules.txt";

    public boolean loadRules(){
        int id = 0;
        List<String> textFromFile = TextFileIO.read(PATH);
        for(String line : textFromFile){
            Rule rule = new Rule();
            rule.id=id++;
            String[] symbols = line.split(" ");
            //checks if valid start of production rule
            if(isVariable(symbols[0])){
                rule.variable=symbols[0];
            } else {
                return false;
            }
            StringBuilder expression = new StringBuilder();
            for(int i=1; i<symbols.length; i++){
                if(symbols[i].equals("|")){
                    if(!expression.toString().equals("")){
                        rule.expressions.add(expression.toString());
                    }
                    expression = new StringBuilder();
                } else {
                    if(!expression.toString().equals("")){
                        expression.append(" ");
                    }
                    expression.append(symbols[i]);
                }
            }
            if(!expression.toString().equals("")){
                rule.expressions.add(expression.toString());
            }
            rules.add(rule);
        }
        return true;
    }

    public void writeRules(){
        List<String> toBeWritten = new ArrayList<>();
        for(Rule rule : rules){
            StringBuilder writtenRuleBuilder = new StringBuilder();
            writtenRuleBuilder.append(rule.variable).append(" ");
            for(String expression : rule.expressions){
                writtenRuleBuilder.append(expression).append(" | ");
            }
            toBeWritten.add(writtenRuleBuilder.substring(0,writtenRuleBuilder.length()-3));
        }
        TextFileIO.write(PATH, toBeWritten);

    }

    public static boolean isVariable(String string){
        if(string.length()>2 && string.charAt(0)=='<' && string.charAt(string.length()-1)=='>'){
          for (int i = 1; i < string.length()-1; i++) {
            if(Character.isLetterOrDigit(string.charAt(i))){
                if(Character.isLowerCase(string.charAt(i))){
                    return false;
                }
            } else {
                return false;
            }
          }
          return true;
        }
        return false;
    }

    public List<Rule> down (Rule rule){
        Set<Rule> downRelated = new HashSet<>();
        List<String> variables = new ArrayList<>();
        for(String expression : rule.expressions){
            String[] split = expression.split(" ");
            for(String str : split){
                if(isVariable(str)){
                    variables.add(str);
                }
            }
        }
        for(String variable : variables){
            rules.forEach(r -> {
                if(r.variable.equals(variable)){
                    downRelated.add(r);
                }
            });
        }
        return new ArrayList<>(downRelated);
    }

    public List<Rule> up (Rule rule){
        Set<Rule> upRelated = new HashSet<>();
        for(Rule r : rules){
            List<Rule> downRelated = down(r);
            if(downRelated.contains(rule)){
             upRelated.add(r);
            }
        }
        return new ArrayList<>(upRelated);
    }

    public void addRule(Rule rule){
        rule.id = rules.get(rules.size()-1).id +1;
        rules.add(rule);
    }

    public void removeRule(int id){
        rules.removeIf(rule -> rule.id==id);
    }

    public List<Rule> getRules() {
        return rules;
    }
}
