package com.example.application.services.chatbot;

import com.example.application.services.utils.TextFileIO;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class CFG {
    public static final ArrayList<Rule> rules = new ArrayList<>();
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/rules.txt";
    private static Rule actionRule = null;
    private static final List<List<String>> allPhrases = new ArrayList<>();
    private static int longestPhraseSize = 0;

    public static boolean loadRules(){
        rules.clear();

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
            List<String> splitExpression = new ArrayList<>();
            for(int i=1; i<symbols.length; i++){
                String symbol = symbols[i];
                if(symbol.equals("|")){
                    if(!expression.toString().equals("")){
                        rule.expressions.add(expression.toString());
                        rule.splitExpressions.add(splitExpression);
                    }
                    expression = new StringBuilder();
                    splitExpression = new ArrayList<>();
                } else {
                    if(!expression.toString().equals("")){
                        expression.append(" ");
                    }
                    expression.append(symbol);
                    splitExpression.add(symbol);
                }
            }
            if(!expression.toString().equals("")){
                rule.expressions.add(expression.toString());
                rule.splitExpressions.add(splitExpression);
            }
            if(rule.variable.equals("<ACTION>")){
                actionRule=rule;
            }
            rules.add(rule);
        }
        combos();
        return true;
    }

    public static void writeRules() throws FileNotFoundException {
        clearFile();
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

    private static void clearFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(PATH);
        writer.print("");
        writer.close();
    }

    public static boolean isVariable(String string){
        return string.length() > 2 && string.charAt(0) == '<' && string.charAt(string.length() - 1) == '>';
    }

    public static List<Rule> down (Rule rule){
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

    public static List<Rule> up (Rule rule){
        Set<Rule> upRelated = new HashSet<>();
        for(Rule r : rules){
            List<Rule> downRelated = down(r);
            if(downRelated.contains(rule)){
             upRelated.add(r);
            }
        }
        return new ArrayList<>(upRelated);
    }

    public static void addRule(Rule rule){
        rule.id = rules.get(rules.size()-1).id +1;
        rules.add(rule);
    }

    public static void removeRule(int id){
        rules.removeIf(rule -> rule.id==id);
    }

    public static List<Rule> getRules() {
        return rules;
    }

    public static List<Rule> getRulesCopy() {
        List<Rule> copy = new ArrayList<>();
        rules.forEach(r -> copy.add(r.copy()));
        return copy;
    }

    public static List<String> getAllActionRules() {
        List<String> result=null;
        String param = null;
        for(Rule r : rules){
            if(r.getVariable().equals("<S>")) param = r.getExpressions().get(0);
        }
        if (param!=null) for(Rule r:rules) if (r.getVariable().equals(param)) result = r.getExpressions();
        return result;
    }

    /**
     * Builds all possible phrases from CFG
     * starting point is the <ACTION> rule
     */
    public static void combos() {
        List<List<String>> unExpressed = new ArrayList<>();
        for(List<String> list : actionRule.splitExpressions){
            List<String> copy = new ArrayList<>(list);
            unExpressed.add(copy);
        }
        while(!unExpressed.isEmpty()){
            List<String> phrase = unExpressed.remove(0);
            boolean fullyExpressed = true;
            for (int i = 0; i < phrase.size(); i++) {
                String word = phrase.get(i);
                if(isVariable(word)){
                    Rule child = findRule(word);
                    for(List<String> expression : child.splitExpressions){
                        unExpressed.add(replace(phrase,expression,i));
                    }
                    fullyExpressed=false;
                }
            }
            if(fullyExpressed){
                boolean duplicate = false;
                for(List<String> list : allPhrases){
                    String target = String.join(" ", list);
                    String word = String.join(" ", phrase);
                    if(target.equals(word)){
                        duplicate=true;
                    }
                }
                if(!duplicate){
                    allPhrases.add(phrase);
                    int total = 0;
                    for(String str : phrase){
                        total += str.length();
                    }
                    longestPhraseSize = Math.max(total,longestPhraseSize);
                }

            }
        }
    }

    private static Rule findRule (String s){
        return rules.stream().filter(r-> r.variable.equals(s)).findFirst().orElse(null);
    }

    private static List<String> replace (List<String> phrase, List<String> expression, int position){
        List<String> str = new ArrayList<>(phrase);
        str.remove(position);
        for(String word : expression){
            if(position>=str.size()){
                str.add(word);
            } else {
                str.add(position,word);
            }
            position++;
        }
        return str;
    }

    public static List<List<String>> getAllPhrases() {
        return allPhrases;
    }

    /**
     * Builds all possible phrases from CFG
     * starting point is the startingVar rule
     */
    public static List<String> combos(String startingVar) {
        List<List<String>> unExpressed = new ArrayList<>();
        unExpressed.add(List.of(startingVar));
        List<String> result = new ArrayList<>();
        while(!unExpressed.isEmpty()){
            List<String> phrase = unExpressed.remove(0);
            boolean fullyExpressed = true;
            for (int i = 0; i < phrase.size(); i++) {
                String word = phrase.get(i);
                if(isVariable(word)){
                    Rule child = findRule(word);
                    for(List<String> expression : child.splitExpressions){
                        unExpressed.add(replace(phrase,expression,i));
                    }
                    fullyExpressed=false;
                }
            }
            if(fullyExpressed){
                String s = "";
                for(String w : phrase){
                    s += w + " ";
                }
                result.add(s.strip());
            }
        }
        return result;
    }

    public static int getLongestPhraseSize() {
        return longestPhraseSize;
    }
}
