package com.example.application.services.camera;


import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class TestSuite {
    public static void main(String[] args) {
        Camera camera = new Camera();
        camera.turnOnCamera();
        boolean faceFound = false;
        SkinColorDetection detector = new SkinColorDetection();
        while (!faceFound) {
            Mat output = detector.detectSkinColor();
            detector.detectFaces(output, 50, 50, 200, 200);
            if (detector.getDetectedFaces() > 0) {
                Imgcodecs.imwrite("output.jpg", detector.getOriginalImage());
                faceFound = true;
            }
            detector.setOriginalImage(camera.captureImage());
        }

    }
}
