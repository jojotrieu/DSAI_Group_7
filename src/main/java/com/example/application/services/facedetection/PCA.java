package com.example.application.services.facedetection;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PCA {
    private List<Mat> trainingImages = new ArrayList<>();
    private List<Integer> labels = new ArrayList<>();


    //TODO: Train using data.
    public void trainRecognizer() {

    }

    //TODO: After it's trained we can project onto the eigenspace and find the most similar image in our database.
    public String recognizeFace(Mat currentImage) {
        return "0";
    }

    public void readData(String path) throws FileNotFoundException {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));

            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(";");
                // Read Images in Gray format
                Mat readImage = Imgcodecs.imread(data[0], 0);
                this.trainingImages.add(readImage);
                // Collect actual labels
                this.labels.add(Integer.parseInt(data[1]));
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
