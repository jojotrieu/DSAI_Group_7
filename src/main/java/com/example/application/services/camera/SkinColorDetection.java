package com.example.application.services.camera;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkinColorDetection {
    private Mat originalImage;

    public SkinColorDetection(BufferedImage orig) {
        this.originalImage = BufferedImageToMat(orig);
    }

    public Mat BufferedImageToMat(BufferedImage original) {
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
        Imgproc.cvtColor(this.originalImage, HSV, Imgproc.COLOR_RGB2HSV);
        Mat YCrCb = new Mat();
        Imgproc.cvtColor(this.originalImage, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        List<Mat> rgbSplit = new ArrayList<>(3);
        Core.split(this.originalImage, rgbSplit);
        List<Mat> hsvSplit = new ArrayList<>(3);
        Core.split(HSV, hsvSplit);
        List<Mat> yCrCbSplit = new ArrayList<>(3);
        Core.split(YCrCb, yCrCbSplit);
        Mat outputMask = new Mat(this.originalImage.rows(), this.originalImage.cols(), CvType.CV_8UC1);

        for (int i = 0; i < originalImage.rows(); i++) {
            for (int j = 0; j < originalImage.cols(); j++) {
                double rValue = rgbSplit.get(2).get(i, j)[0];
                double gValue = rgbSplit.get(1).get(i, j)[0];
                double bValue = rgbSplit.get(0).get(i, j)[0];

                double hValue = hsvSplit.get(0).get(i, j)[0];

                double crValue = yCrCbSplit.get(1).get(i, j)[0];
                double cbValue = yCrCbSplit.get(2).get(i, j)[0];

                if (RGBRule(rValue, gValue, bValue) && HSVRule(hValue) && YCrCbRule(crValue, cbValue)) {
                    outputMask.put(i, j, 255);
                } else {
                    outputMask.put(i, j, 0);
                }
            }
        }


        return new Mat();
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
        return h > 50 && h < 150;
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
}
