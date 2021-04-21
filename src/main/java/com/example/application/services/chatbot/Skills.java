package com.example.application.services.chatbot;

import com.example.application.services.utils.TextFileIO;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Skills {
    public static ArrayList<Action> actions = new ArrayList<>();
    private static final String PATH = "src/main/java/com/example/application/services/chatbot/actions.txt";

    public static void loadActions(){
        actions.clear();

        int id = 0;
        List<String> textFromFile = TextFileIO.read(PATH);
        for(String line : textFromFile){
            Action action = new Action();
            action.id=id++;
            String[] symbols = line.split(" ");
            int startIndex=1;

            //in case no variable was specified in file -> defaults to <ACTION>
            if(CFG.isVariable(symbols[0])){
                action.variable=symbols[0];
            } else {
                startIndex=0;
                action.variable="<ACTION>";
            }
            //checks if second symbol is '*' -> if so, starts filling the Action's hashmap
            if(symbols[1].equals("*")){
                for (int i = 2; !symbols[i].equals("*"); i++) {
                    String nonTerminal = symbols[i];
                    if(CFG.isVariable(nonTerminal)){
                        StringBuilder valueBuilder = new StringBuilder();
                        for (i+=1; !CFG.isVariable(symbols[i]) && !symbols[i].equals("*"); i++) {
                            valueBuilder.append(symbols[i]).append(" ");
                        }
                        if(valueBuilder.length()>0){
                            valueBuilder.deleteCharAt(valueBuilder.length()-1);
                        }
                        String value = valueBuilder.toString();
                        action.nonTerminals.put(nonTerminal,value);
                        i--;
                    }
                    startIndex=i+2;
                }
            }

            StringBuilder expression = new StringBuilder();
            for(int i=startIndex; i<symbols.length; i++){
                    expression.append(symbols[i]).append(" ");
            }
            action.expression=expression.toString();
            actions.add(action);
        }
    }

    public static boolean isValidAction(Action action){
        if(!CFG.isVariable(action.variable)){
            return false;
        }
        if(!action.nonTerminals.keySet().stream().allMatch(CFG::isVariable) && !action.nonTerminals.isEmpty()){
            return false;
        }
        for(String value : action.nonTerminals.values()){
            String[] symbols = value.split(" ");
            for(String s : symbols){
                if(CFG.isVariable(s)){
                    return false;
                }
            }
        }
        String[] symbols = action.expression.split(" ");
        if(Arrays.stream(symbols).anyMatch(CFG::isVariable)){
            return false;
        }
        if(action.expression.length()==0){
            return false;
        }
        return true;
    }

    public static void writeActions() throws FileNotFoundException {
        clearFile();
        List<String> toBeWritten = new ArrayList<>();
        for(Action action : actions){
            StringBuilder lineBuilder = new StringBuilder();
            lineBuilder.append(action.variable).append(" ");
            if(!action.nonTerminals.isEmpty()){
                lineBuilder.append("* ");
                for(Map.Entry<String,String> entry : action.nonTerminals.entrySet()){
                    lineBuilder.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ");
                }
                lineBuilder.append("* ");
            }
            toBeWritten.add(lineBuilder + action.expression);
        }
        TextFileIO.write(PATH, toBeWritten);

    }

    private static void clearFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(PATH);
        writer.print("");
        writer.close();
    }

    public static boolean addAction(Action action){
        if(isValidAction(action)){
            action.id = actions.get(actions.size()-1).id +1;
            actions.add(action);
            return true;
        }
        return false;
    }

    public static void removeAction(int id){
        actions.removeIf(action -> action.id==id);
    }

    public static List<Action> getActions() {
        return actions;
    }

    public static List<Action> getActionsCopy(){
        List<Action> actionsCopy = new ArrayList<>();
        actions.forEach(a -> actionsCopy.add(a.copy()));
        return actionsCopy;
    }
}
