package com.example.application.services.facedetection;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class TestPCA {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        Mat converted = Imgcodecs.imread("RecognizerDB/adele14.jpg");

        PCA pca = new PCA();
        pca.readData(".\\RecognizerDB\\TrainDataPath.txt");
        pca.trainRecognizer();
        String output = pca.recognizeFace(converted);
        System.out.println("Hello, " + output + " !");
    }
}
