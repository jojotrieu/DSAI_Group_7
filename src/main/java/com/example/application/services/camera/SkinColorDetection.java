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

    public Mat BufferedImageToMat(BufferedImage original){
        Mat converted = new Mat(original.getHeight(), original.getWidth(), CvType.CV_8UC3);
        byte[] imageInfo = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        converted.put(0, 0, imageInfo);
        return converted;
    }

    public BufferedImage Mat2BufferedImage(Mat matrix) throws IOException {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    public Mat detectSkinColor(){
        Mat HSV = new Mat();
        Imgproc.cvtColor(this.originalImage,HSV, Imgproc.COLOR_RGB2HSV);
        Mat YCrCb = new Mat();
        Imgproc.cvtColor(this.originalImage,YCrCb,Imgproc.COLOR_RGB2YCrCb);
        List<Mat> rgbSplit = new ArrayList<>(3);
        Core.split(this.originalImage,rgbSplit);
        List<Mat> hsvSplit = new ArrayList<>(3);
        Core.split(HSV,hsvSplit);
        List<Mat> yCrCbSplit = new ArrayList<>(3);
        Core.split(YCrCb,yCrCbSplit);


        return new Mat();
    }

}
