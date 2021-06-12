package com.example.application.services.facedetection;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;

public class ImageGrabber {
    // Class to grab images for our PCA database
    // Try to move and chose different position/lightning for every grabbed image
    public static void main(String[] args) {
        Camera camera = new Camera();
        camera.turnOnCamera();
        BufferedImage img = camera.captureImage();
        camera.closeCamera();
        SkinColorDetection det = new SkinColorDetection();
        OpenCV.loadLocally();
        Mat converted = det.BufferedImageToMat(img);
        // Change the folder to your name and keep the file name in image1-10 format
        Imgcodecs.imwrite("RecognizerDB/corina6.jpg", converted);
    }
}
