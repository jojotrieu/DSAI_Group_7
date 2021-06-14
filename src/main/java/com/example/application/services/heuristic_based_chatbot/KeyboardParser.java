import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class KeyboardParser {
    //Assume a file in the following format.
    //<keycap>: <list of neighbouring keycaps separated by a comma>
    // e.g a: q,w,s,z
    private File file;
    private Scanner scanner;
    private Keyboard keyboard;
    public KeyboardParser(String filePath) {
        this.file = new File(filePath);
        try {
            this.scanner = new Scanner(this.file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        parse();
        this.scanner.close();
    }

    public Keyboard getKeyboard(){
        return this.keyboard;
    }

    private void parse() {
        this.keyboard = new Keyboard();
        while(this.scanner.hasNextLine()) {
            // We retrieve the first symbol which is the keycap itself:
            String line = this.scanner.nextLine().toLowerCase().replaceAll("\\s+","");
            //System.out.println("Line: " + line);
            String symbol = line.charAt(0) + ""; // "" used to convert it to a string
            String[] neighbours = line.substring(2).split(",");
            //System.out.println("Symbol: " + symbol);
            Keycap keycap = new Keycap(symbol);
            // We find the neighbours of that new keycap:
            for (int i = 0; i < neighbours.length; i++) {
                //System.out.println("Neighbours: " + neighbours[i]);
                // We add them one by one:
                Keycap neighbour = new Keycap(neighbours[i]);
                keycap.addneighbouringKeycap(neighbour);
            }
            // Once the keycap and its neighbours have been added we add it to the keyboard:
            this.keyboard.addKeycap(keycap);
        }
    }
}
