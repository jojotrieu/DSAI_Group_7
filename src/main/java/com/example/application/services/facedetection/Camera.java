package com.example.application.services.facedetection;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.server.StreamResource;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

@Service
public class Camera {
    private final HaarCascadeDetector haarCascadeDetector = new HaarCascadeDetector();
    private BufferedImage imageToAnalyze;
    private List<DetectedFace> faces = new ArrayList<>();
    private int facesCount;
    private Webcam webcam;


    public void turnOnCamera() {
        webcam = Webcam.getWebcams().get(0);
        Dimension viewSize = new Dimension(WebcamResolution.VGA.getSize().width / 2,
                WebcamResolution.VGA.getSize().height / 2);
        webcam.setViewSize(viewSize);
        webcam.open();
    }

    public BufferedImage captureImage() {
        imageToAnalyze = webcam.getImage();
        this.facesCount = 0;
        return imageToAnalyze;
    }

    public void closeCamera() {
        webcam.close();
    }

    public StreamResource generateUiImage(BufferedImage image) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bos);
            return new StreamResource("webcamCapture.png", () -> new ByteArrayInputStream(bos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage detectFaces() {
        this.faces = haarCascadeDetector.detectFaces(ImageUtilities.createFImage(imageToAnalyze));
        this.facesCount = faces.size();
        BufferedImage newImage = imageToAnalyze;
        Graphics2D g2 = newImage.createGraphics();
        Stroke STROKE = new BasicStroke(2f);
        for (DetectedFace face : faces) {
            Rectangle faceBounds = face.getBounds();

            double dx = 0.05 * faceBounds.width;
            double dy = 0.1 * faceBounds.height;
            double x = faceBounds.x - dx;
            double y = faceBounds.y - dy;
            double w = faceBounds.width + dx;
            double h = faceBounds.height + 2 * dy;

            g2.setStroke(STROKE);
            g2.setColor(Color.YELLOW);
            g2.drawRect((int) x, (int) y, (int) w, (int) h);
        }
        return newImage;
    }


    public int getFacesCount() {
        return facesCount;
    }

    public BufferedImage getImageToAnalyze(){
        return this.imageToAnalyze;
    }
}
