package com.example.application.services.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;

//TODO: Extract camera stream
public class Camera {

    public BufferedImage openCamera() {
        Webcam webcam = Webcam.getDefault();
        Dimension viewSize = new Dimension(WebcamResolution.VGA.getSize().width / 2,
                WebcamResolution.VGA.getSize().height / 2);
        webcam.setViewSize(viewSize);
        webcam.open();
        return webcam.getImage();
    }

    public StreamResource createImage() {


        BufferedImage cameraPic = openCamera();

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(cameraPic, "png", bos);
            return new StreamResource("webcamCapture.png", () -> new ByteArrayInputStream(bos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
