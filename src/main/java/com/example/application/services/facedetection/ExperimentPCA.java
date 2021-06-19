package com.example.application.services.facedetection;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentPCA {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        PCA pca = new PCA();
        List<Mat> testImages = new ArrayList<>();
        List<Integer> actualLabels = new ArrayList<>();
        pca.readData(".\\CalTech_Faces1999\\path.txt");
        readTestData(".\\CalTech_Faces1999\\test.txt", testImages, actualLabels);
        long startTime = System.currentTimeMillis();
        pca.trainRecognizer();
        long estimatedTime = System.currentTimeMillis() - startTime;
        double correctlyPredicted = 0;
        long predictStart = System.currentTimeMillis();
        for (int i = 0; i < testImages.size(); i++) {
            int actualLabel = actualLabels.get(i);
            int predictedLabel = pca.predictLabelInt(testImages.get(i));
            System.out.println("Actual Label : " + actualLabel + " Predicted Label : " + predictedLabel);
            if (actualLabel == predictedLabel) {
                correctlyPredicted++;
            }
        }
        long predictEnd = System.currentTimeMillis() - predictStart;
        double accuracy =  (correctlyPredicted / testImages.size()) * 100;
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Training time: " + estimatedTime + " milliseconds");
        System.out.println("Prediction time: " + predictEnd + " milliseconds");
    }

    public static void readTestData(String path, List<Mat> testImg, List<Integer> labels) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));

            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(";");
                // Read Images in Gray format
                Mat readImage = Imgcodecs.imread(data[0]);
                Mat holdImage = new Mat();
                Imgproc.cvtColor(readImage, holdImage, Imgproc.COLOR_BGR2GRAY);
                testImg.add(holdImage);
                // Collect actual labels
                labels.add(Integer.parseInt(data[1]));
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
