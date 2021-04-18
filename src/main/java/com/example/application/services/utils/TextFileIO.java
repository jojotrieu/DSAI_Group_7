package com.example.application.services.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextFileIO {
    public static List<String> read(String path){
        List<String> text = new ArrayList<>();
        try {
            File textFile = new File(path);
            Scanner myReader = new Scanner(textFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                text.add(data);
            }
            myReader.close();
            System.out.println(path + ": read.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return text;
    }

    public static void write(String path, List<String> textToWrite){
        try {
            FileWriter myWriter = new FileWriter(path, false);
            for (String str : textToWrite) {
                myWriter.write(str);
                myWriter.write(System.lineSeparator());
            }
            myWriter.close();
            System.out.println(path + ": written.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
