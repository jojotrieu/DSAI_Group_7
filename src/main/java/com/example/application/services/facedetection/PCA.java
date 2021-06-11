package com.example.application.services.facedetection;

import aist.science.aistcv.AistCVLoader;
import com.lowagie.text.html.simpleparser.Img;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.CvType.CV_32SC1;
import static org.opencv.core.CvType.channels;

public class PCA {
    private List<Mat> trainingImages = new ArrayList<>();
    private List<Integer> labelList = new ArrayList<>();
    private BasicFaceRecognizer faceRecognizer;

    public void trainRecognizer() {
        AistCVLoader.loadLocally();
        faceRecognizer = EigenFaceRecognizer.create(1000,13000);
        Mat labels = new Mat(labelList.size(), 1, CV_32SC1);
        for (int i = 0; i < labelList.size(); i++) {
            labels.put(i, 0, labelList.get(i));
        }
        faceRecognizer.train(trainingImages, labels);
    }

    public String recognizeFace(Mat currentImage) {
        Imgproc.cvtColor(currentImage, currentImage, Imgproc.COLOR_BGR2GRAY);
        int label = faceRecognizer.predict_label(currentImage);
        System.out.println(label);
        if (label == 1) {
            return "Alex";
        } else if (label == 2) {
            return "Corina";
        } else if (label == 3) {
            return "Adele";
        }
        else{
            return "I can't recognize you!";
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
                Imgcodecs.imwrite("adele.jpg",readImage);
                // Collect actual labels
                this.labelList.add(Integer.parseInt(data[1]));
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
