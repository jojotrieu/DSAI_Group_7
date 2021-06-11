package com.example.application.services.facedetection;

import org.opencv.core.Mat;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32SC1;

public class PCA {
    private List<Mat> trainingImages = new ArrayList<>();
    private List<Integer> labelList = new ArrayList<>();
    private BasicFaceRecognizer faceRecognizer = EigenFaceRecognizer.create(80);


    public void trainRecognizer() {
        Mat labels = new Mat(labelList.size(), 1, CV_32SC1);
        for (int i = 0; i < labelList.size(); i++) {
            labels.put(i, 1, labelList.get(i));
        }
        faceRecognizer.train(trainingImages, labels);
    }

    public String recognizeFace(Mat currentImage) {
        int label = faceRecognizer.predict_label(currentImage);
        if (label == 1) {
            return "Alex";
        } else if (label == 2) {
            return "Corina";
        } else {
            return "I can't recognize you!";
        }
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
                this.labelList.add(Integer.parseInt(data[1]));
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
