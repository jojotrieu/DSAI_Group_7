package com.example.application.services.camera;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class TestSuite {
    public static void main(String[] args) {

        SkinColorDetection detector = new SkinColorDetection(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));
        Mat output = detector.detectSkinColor();
        Imgcodecs.imwrite("output.jpg",detector.getOriginalImage());

    }
}
