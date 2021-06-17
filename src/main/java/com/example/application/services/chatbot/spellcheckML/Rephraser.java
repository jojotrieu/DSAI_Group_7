package com.example.application.services.chatbot.spellcheckML;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.utils.TextFileIO;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Rephraser {
    private static List<String> rephrased = new ArrayList<>();
    private static String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789 ";
    private static int space = alphabet.indexOf(' ')+100;

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
            if(alphabet.indexOf(word.charAt(0))<26){
                if (rand < .3) {
                    split.set(index, Misspeller.mutate(new StringBuilder(word)));
                } else if (rand < .6) {
                    split.remove(index);
                    split.add((int) (Math.random() * (double) split.size()), word);
                }
            }
            builder.append(split.remove(0)).append(" ");
        }
        return builder.toString();
    }

    public static List<String> getRephrased() {
        return rephrased;
    }

    public static void writeSeq2Disk(){
        String path = "src/main/java/com/example/application/services/chatbot/spellcheckML/phraseVariations.csv";

        List<String> variations = new ArrayList<>();
        for(String phrase : rephrased){
            String [] split = phrase.split("\\W+");
            StringBuilder stringBuilder = new StringBuilder();
            int len = 0;
            int label = 0;
            for(String word : split){
                int firstChar = alphabet.indexOf(word.charAt(0));
                if(firstChar>25){
                    int value = Integer.parseInt(word);
                    if(value>=50000){
                        label = value-50000;
                        break;
                    }
                }
                for (int i = 0; i < word.length(); i++) {
                    int value = alphabet.indexOf(word.charAt(i));
                    stringBuilder.append(value+100).append(" ");
                    len++;
                }
            }
            while(len<90){
                stringBuilder.append(space).append(" ");
                len++;
            }
            stringBuilder.append(label);
            variations.add(stringBuilder.toString());
        }
        TextFileIO.write(path,variations);
    }

    public static INDArray convert (String phrase){
        String [] split = phrase.split("\\W+");
        int len = 0;
        int[] arr  = new int[90];
        for(String word : split) {
            for (int i = 0; i < word.length(); i++) {
                int value = alphabet.indexOf(word.charAt(i));
                arr[len] = (value+100);
                len++;
            }
        }
        while(len<90){
            arr[len] = space;
            len++;
        }
        INDArray vector = Nd4j.createFromArray(arr);
        return vector;
    }
}
