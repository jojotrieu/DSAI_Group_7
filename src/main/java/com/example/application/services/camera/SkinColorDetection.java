package com.example.application.services.camera;


import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkinColorDetection {
    private Mat originalImage;
    private int detectedFaces;

    public Mat BufferedImageToMat(BufferedImage orig) {
        // Convert webcam image to BGR because OpenCV reads images as BGR instead of RGB
        OpenCV.loadLocally();
        BufferedImage original = new BufferedImage(orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        original.getGraphics().drawImage(orig, 0, 0, null);
        Mat converted = new Mat(original.getHeight(), original.getWidth(), CvType.CV_8UC3);
        byte[] imageInfo = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        converted.put(0, 0, imageInfo);
        return converted;
    }

    public BufferedImage Mat2BufferedImage(Mat matrix) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    public Mat detectSkinColor() {
        Mat HSV = new Mat();
        Imgproc.cvtColor(this.originalImage, HSV, Imgproc.COLOR_BGR2HSV);
        Mat YCrCb = new Mat();
        Imgproc.cvtColor(this.originalImage, YCrCb, Imgproc.COLOR_BGR2YCrCb);
        Mat outputMask = new Mat(this.originalImage.rows(), this.originalImage.cols(), CvType.CV_8UC1);

        for (int i = 0; i < originalImage.rows(); i++) {
            for (int j = 0; j < originalImage.cols(); j++) {
                double[] rgbValues = this.originalImage.get(i, j);
                double rValue = rgbValues[2];
                double gValue = rgbValues[1];
                double bValue = rgbValues[0];

                double hValue = HSV.get(i, j)[0];

                double crValue = YCrCb.get(i, j)[1];
                double cbValue = YCrCb.get(i, j)[2];

                if (RGBRule(rValue, gValue, bValue) && HSVRule(hValue) && YCrCbRule(crValue, cbValue)) {
                    outputMask.put(i, j, 255);
                } else {
                    outputMask.put(i, j, 0);
                    /* byte[] black = {0,0,0};
                    this.originalImage.put(i,j,black); */
                }
            }
        }


        return outputMask;
    }

    public void detectFaces(Mat skinMask, int minHeight, int minWidth, int maxHeight, int maxWidth) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(skinMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint contour : contours) {
            Rect faceRectangle = Imgproc.boundingRect(contour);
            if (faceRectangle.width > minWidth && faceRectangle.height > minHeight &&
                    faceRectangle.width < maxWidth && faceRectangle.height < maxHeight) {
                Rect newBounds = new Rect(faceRectangle.x, faceRectangle.y,
                        faceRectangle.width, (int) (faceRectangle.width * 1.25));
                Imgproc.rectangle(this.originalImage, newBounds, new Scalar(128, 0, 128), 2);
                detectedFaces++;
            }
        }
    }

    public boolean RGBRule(double r, double g, double b) {
        boolean ruleOne = false;
        boolean ruleTwo = false;
        double rgbMax = Math.max(Math.max(r, g), b);
        double rgbMin = Math.min(Math.min(r, g), b);

        // Uniform daylight illumination
        if (r > 95 && g > 40 && b > 20 && (rgbMax - rgbMin) > 15 && Math.abs(r - g) > 15 && r > g && r > b) {
            ruleOne = true;
        }
        // Flashlight or daylight lateral illumination
        if (r > 220 && g > 210 && b > 170 && Math.abs(r - g) <= 15 && b < r && b < g) {
            ruleTwo = true;
        }


        return ruleOne || ruleTwo;
    }

    public boolean HSVRule(double h) {
        return h < 50 || h > 150;
    }

    public boolean YCrCbRule(double cr, double cb) {
        boolean rule = false;
        double lineOne = 1.5862 * cb + 20;
        double lineTwo = 0.3448 * cb + 76.2069;
        double lineThree = -1.005 * cb + 234.5652;
        double lineFour = -1.15 * cb + 301.75;
        double lineFive = -2.2857 * cb + 432.85;

        if (cr <= lineOne && cr >= lineTwo && cr >= lineThree && cr <= lineFour && cr <= lineFive) {
            rule = true;
        }

        return rule;
    }

    public Mat getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage capture) {
        this.originalImage = BufferedImageToMat(capture);
    }

    public int getDetectedFaces() {
        return detectedFaces;
    }
}
