package com.example.application.services.camera;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class TestSuite {
    public static void main(String[] args) {
        Camera camera = new Camera();
        BufferedImage snapshot = camera.captureImage();
        SkinColorDetection detector = new SkinColorDetection(snapshot);
        Mat output = detector.detectSkinColor();
        Imgcodecs.imwrite("output.jpeg",output);

    }
}
