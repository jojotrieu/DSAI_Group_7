package com.example.application.services.facedetection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WritePath {
    public static void main(String[]args) throws IOException {
        String paths = "";

        for(int i = 99; i < 450; i++){
            paths = paths + ("CalTech_Faces1999/image_0" + (i+1) + ".jpg;0") + "\n";
        }
        String fileName = ".\\CalTech_Faces1999\\path.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append(paths);
        writer.close();
    }
}
