package com.example.application.services.facedetection;

import aist.science.aistcv.AistCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.opencv.core.CvType.CV_32SC1;

public class PCA {
    private final List<Mat> trainingImages = new ArrayList<>();
    private final List<Integer> labelList = new ArrayList<>();
    private List<String> labelNames = new ArrayList<>();
    private BasicFaceRecognizer faceRecognizer;

    public void trainRecognizer() {
        AistCVLoader.loadLocally();
        faceRecognizer = EigenFaceRecognizer.create(1000, 15000);
        Mat labels = new Mat(labelList.size(), 1, CV_32SC1);
        for (int i = 0; i < labelList.size(); i++) {
            labels.put(i, 0, labelList.get(i));
        }
        faceRecognizer.train(trainingImages, labels);
    }

    public String recognizeFace(Mat currentImage) {
        Mat newImage = new Mat();
        Imgproc.cvtColor(currentImage, newImage, Imgproc.COLOR_BGR2GRAY);
        if (newImage.rows() != 240 || newImage.cols() != 320) {
            Imgproc.resize(newImage, newImage, new Size(320, 240));
        }
        int label = faceRecognizer.predict_label(newImage);
        System.out.println(label);

        labelNames = new ArrayList<>(new LinkedHashSet<>(labelNames));

        if (label > 0) {
            return labelNames.get(label - 1);
        } else {
            return "Unknown";
        }
    }

    public void readData(String path) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));

            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(";");
                // Read Images in Gray format
                Mat readImage = Imgcodecs.imread(data[0]);
                Imgproc.cvtColor(readImage, readImage, Imgproc.COLOR_BGR2GRAY);
                if (readImage.rows() != 240 || readImage.cols() != 320) {
                    Imgproc.resize(readImage, readImage, new Size(320, 240));
                }
                this.trainingImages.add(readImage);
                // Collect actual labels
                this.labelList.add(Integer.parseInt(data[1]));
                this.labelNames.add(retrieveName(data[0]));
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String retrieveName(String imagePath) {
        int startIndex = 13;
        int endIndex = imagePath.length() - 1;

        for (int i = 0; i < imagePath.length(); i++) {
            if (Character.isDigit(imagePath.charAt(i))) {
                endIndex = i;
                break;
            }
        }

        char firstLetter = Character.toUpperCase(imagePath.charAt(startIndex));
        return firstLetter + imagePath.substring(startIndex + 1, endIndex);
    }
}
