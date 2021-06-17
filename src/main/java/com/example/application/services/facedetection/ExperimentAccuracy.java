package com.example.application.services.facedetection;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Class to run the face detection algorithms on the entire CalTech database and output the images after the faces
// were detected

public class ExperimentAccuracy {
    public static void main(String[] args) throws IOException {
        BufferedReader br;
        List<BufferedImage> testImages = new ArrayList<>();
        OpenCV.loadLocally();
        try {
            br = new BufferedReader(new FileReader(".\\CalTech_Faces1999\\path.txt"));

            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(";");
                // Read Images in Gray format
                BufferedImage readImage2 = ImageIO.read(new File(data[0]));
                testImages.add(readImage2);
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        SkinColorDetection detection = new SkinColorDetection();
        Camera camera = new Camera();

        int detectedFaces_SCD = 0;
        int detectedFaces_HCC = 0;
        int count = 1;

        for (BufferedImage testImage : testImages) {
            detection.setOriginalImage(testImage);
            Mat mask = detection.detectSkinColor();
            detection.detectFaces(mask, 160, 140, 600, 500);
            detectedFaces_SCD += detection.getDetectedFaces();
            Imgcodecs.imwrite("testSCD/test" + count + ".jpg", detection.getOriginalImage());
            camera.setImageToAnalyze(testImage);
            camera.detectFaces();
            detectedFaces_HCC += camera.getFacesCount();
            String path = "./testHCC/test" + count + ".jpg";
            ImageIO.write(camera.getImageToAnalyze(), "jpg", new File(path));
            count++;
        }

        System.out.println(detectedFaces_SCD);
        System.out.println(detectedFaces_HCC);

    }
}
