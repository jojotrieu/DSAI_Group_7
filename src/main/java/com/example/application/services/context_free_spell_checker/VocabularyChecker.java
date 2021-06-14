import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VocabularyChecker {
    private File file;
    private Scanner scanner;
    private List<String> dictionary;
    public VocabularyChecker(String filePath) {
        this.dictionary = new ArrayList<String>();
        this.file = new File(filePath);
        try {
            this.scanner = new Scanner(this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        parse();
        this.scanner.close();
    }

    private void parse() {
        while(this.scanner.hasNextLine()) {
            this.dictionary.add(this.scanner.nextLine());
        }
    }

    public boolean isInDictionary(String word){
        for (String w: this.dictionary) {
            if(word.toLowerCase().equals(w)){
                return true;
            }
        }
        return false;
    }

    public String closestMatch(String word){
        String closestMatch = this.dictionary.get(0);
        int closestMatchIndex = 0;
        double smallestDistance = evaluate(word, this.dictionary.get(0));
        for (int i = 0; i < this.dictionary.size(); i++) {
            if(evaluate(word, this.dictionary.get(i)) < smallestDistance){
                smallestDistance = evaluate(word, this.dictionary.get(i));
                closestMatchIndex = i;
                closestMatch = this.dictionary.get(i);
            }
        }
        return closestMatch;
    }

    /**
     * Levenshtein distance between two words
     * Mainly taken from https://www.baeldung.com/java-levenshtein-distance
     * It calculates the distance between two words by taking into account the number of permutations, additions, and deletions to get to the other word.
     * @param word1 the first word
     * @param word2 the second word
     * @return the distance between those two words using levenshtein'distance.
     */
    private int evaluate(String word1, String word2){
        int[][] array = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i <= word1.length(); i++) {
            for (int j = 0; j <= word2.length(); j++) {
                if (i == 0) {
                    array[i][j] = j;
                }
                else if (j == 0) {
                    array[i][j] = i;
                }
                else {
                    array[i][j] = Math.min(array[i - 1][j - 1] + costOfSubstitution(word1.charAt(i - 1), word2.charAt(j - 1)),Math.min(array[i - 1][j] + 1, array[i][j - 1] + 1));
                }

                }
            }
            return array[word1.length()][word2.length()];
        }

        private int costOfSubstitution(char character1, char character2) {
            if(character1 == character2) {
                return 0;
            }else{
                return 1;
            }
        }

}
