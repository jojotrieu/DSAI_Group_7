package com.example.application.services.facedetection;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class TestPCA {
    public static void main(String[] args) {
        Camera camera = new Camera();
        camera.turnOnCamera();
        BufferedImage img = camera.captureImage();
        camera.closeCamera();
        SkinColorDetection det = new SkinColorDetection();
        OpenCV.loadLocally();
        Mat converted = det.BufferedImageToMat(img);

        PCA pca = new PCA();
        pca.readData("C:\\Users\\dolbi\\Desktop\\DSAI_Group_7\\RecognizerDB\\TrainDataPath.txt");
        pca.trainRecognizer();
        String output = pca.recognizeFace(converted);
        System.out.println("Hello, " + output + " !");
    }
}
