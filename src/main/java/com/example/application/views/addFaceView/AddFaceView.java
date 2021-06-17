package com.example.application.views.addFaceView;

import com.example.application.services.facedetection.Camera;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "addFace", layout = MainView.class)
@CssImport("./styles/views/addFace/addFaceView.css")
@PageTitle("Add Face")
public class AddFaceView extends Div {

    private final Camera camera = new Camera();
    private final TextField name = new TextField("Name:");
    private Image image = new Image();
    private final Button snapshot = new Button("Snapshot");
    private List<BufferedImage> temporaryFaces = new ArrayList<>();
    private final int nrOfImages = 15;
    private int count = 0;
    private final String counter = "/"+nrOfImages;
    private final Button saveButton = new Button("SAVE FACE");
    private final Label message = new Label();

    public AddFaceView() {
        message.setText("Start by filling your name. Then, take 15 pictures of yourself. Try to change the light and your facial expression.");
        message.setId("message");
        setId("addFace-view");
        name.setId("name");
        add(message);
        add(name);
        initSnapshotButton();
        add(snapshot);
        Label label = new Label(count+counter);
        label.setId("counter");
        add(label);
        saveButton.setId("save-button");
        initSaveButton();
        add(saveButton);
        saveButton.setEnabled(false);
    }

    private void initSaveButton() {
        saveButton.addClickListener(e->{
            snapshot.setEnabled(false);
            name.setReadOnly(true);

            count = 1;
            for(BufferedImage cameraSnapshot: temporaryFaces){
                String path = "./RecognizerDB/"+name.getValue().toLowerCase()+count+".jpg";
                File file = new File(path);
                try {
                    ImageIO.write(cameraSnapshot, "jpg", file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                count++;
            }

            Label success = new Label(name.getValue()+"'s face has been saved.");
            success.setId("success-message");
            add(success);
        });
    }

    private void initSnapshotButton(){
        snapshot.setId("snapshot-button");
        snapshot.addClickListener(e->{
            camera.turnOnCamera();
            BufferedImage cameraSnapshot = camera.captureImage();
            camera.closeCamera();
            StreamResource streamResource = camera.generateUiImage(cameraSnapshot);
            image = new Image(streamResource, "capture");
            image.setId("camera-frame");
            count++;
            temporaryFaces.add(cameraSnapshot);
            
            Label label = new Label(count+counter);
            if(label.getText().equals(nrOfImages+"/"+nrOfImages)){
                saveButton.setEnabled(true);
            }
            refresh();
        });
    }

    private void refresh(){
        removeAll();
        add(message);
        add(name);
        add(image);
        add(snapshot);
        Label label = new Label(count+counter);
        label.setId("counter");
        add(label);
        add(saveButton);
    }
}
