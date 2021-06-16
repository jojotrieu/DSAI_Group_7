package com.example.application.services.chatbot.spellcheckML;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.utils.TextFileIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Rephraser {
    private static List<String> rephrased = new ArrayList<>();

    public static void fill(int augmentation){
        HashSet<String> set = new HashSet<>();
        List<String> actions = CFG.getAllActionRules();
        for (int i = 0; i < actions.size(); i++) {
            String action = actions.get(i);
            List<String> phrases = CFG.combos(action);
            int index = 50000+i;
            for(String phrase : phrases){
                phrase = phrase.toLowerCase();
                String label = " " + index;
                set.add(phrase + label);
                for (int j = 0; j < augmentation; j++) {
                    String mutation = mutate(phrase);
                    if(!set.add(mutation + label)){
                        j--;
                    }
                }
            }
        }
        rephrased = new ArrayList<>(set);
    }

    public static String mutate(String phrase) {
        List<String> split = new ArrayList<>(Arrays.asList(phrase.split("\\W+")));
        StringBuilder builder = new StringBuilder();
        while (!split.isEmpty()) {
            int index = (int) (Math.random() * (double) split.size());
            String word = split.get(index);
            double rand = Math.random();
            if (rand < .3) {
                split.set(index, Misspeller.mutate(new StringBuilder(word)));
            } else if (rand < .6) {
                split.remove(index);
                split.add((int) (Math.random() * (double) split.size()), word);
            }
            builder.append(split.remove(0)).append(" ");
        }
        return builder.toString();
    }

    public static List<String> getRephrased() {
        return rephrased;
    }

    public static void write2Disk(){
        TextFileIO.write("src/main/java/com/example/application/services/chatbot/spellcheckML/phraseVariations.txt",rephrased);
    }
}
