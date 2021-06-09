package com.example.application.views.chatbotview;

import com.example.application.services.ChatBot;
import com.example.application.services.facedetection.Camera;
import com.example.application.services.facedetection.SkinColorDetection;
import com.example.application.views.main.MainView;
import com.example.application.views.settingsview.SettingsView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Image;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.IOException;


@Route(value = "chatbot", layout = MainView.class)
@PageTitle("ChatBot")
@CssImport("./styles/views/chatbot/chatbot.css")
@RouteAlias(value = "", layout = MainView.class)
public class ChatBotView extends HorizontalLayout {
    private static int reloadCounter = 0;
    private TextField questionTextField;
    private Dialog cameraPopUp = new Dialog();
    private Button clearButton = new Button("Clear Chat");
    private Button analyzeButton = new Button("Analyze!");
    private Button retakeImage = new Button("Retake!");
    private Button snapshot = new Button("Snapshot!");
    private Button cameraCheck = new Button("Camera Check");
    private H4 thinking = new H4("ChatBot: Mmmm... Let me think.");
    private TextArea area = new TextArea();
    private String conversation = "";
    private Camera camera = new Camera();

    public ChatBotView() {
        setId("chatbot-view");
        area.setReadOnly(true);
        area.setId("text-area");
        questionTextField = new TextField("Ask me anything");
        questionTextField.setId("question-field");
        clearButton.setId("clear-button");
        cameraCheck.setId("camera-button");
        analyzeButton.setId("analyze-button");
        retakeImage.setId("retake-button");
        cameraPopUp.setWidth("500px");
        cameraPopUp.setHeight("500px");
        questionTextField.setEnabled(false);
        questionTextField.addKeyPressListener(Key.ENTER, e -> {
            //disable Text Field while ChatBot is thinking
            questionTextField.setEnabled(false);
            //display question in H4
            String questionH4 = "You: " + questionTextField.getValue();
            conversation += questionH4 + "\n";
            //display "thinking" while ChatBot is thinking
            add(thinking);
            //get response from chatBot
            String responseString = ChatBot.respondTo(questionTextField.getValue());
            String responseH4 = "ChatBot: " + responseString;
            conversation += responseH4 + "\n";
            area.setValue(conversation);
            //clearButton Text Field
            questionTextField.clear();
            //re-enable Text Field
            questionTextField.setEnabled(true);
            remove(thinking);
            clearButton.setEnabled(true);
        });
        add(cameraCheck);
        add(questionTextField);
        add(clearButton);
        add(area);
        clearButton.addClickListener(e -> {
            conversation = "";
            area.setValue(conversation);
            clearButton.setEnabled(false);
        });
        clearButton.setEnabled(false);

        if (SettingsView.selectedType.equalsIgnoreCase("Login check")) {
            cameraCheck.setEnabled(true);
            setUpLoginCheck();
        } else {

            backgroundCheck();
        }


    }

    public void setUpLoginCheck() {
        cameraCheck.addClickListener(e -> {
            cameraPopUp.removeAll();
            snapshot.setId("snapshot-button");
            cameraPopUp.add(snapshot);
            cameraPopUp.open();
        });

        snapshotButton(snapshot);

        snapshotButton(retakeImage);

        analyzeButton.addClickListener(e -> {
            cameraPopUp.removeAll();
            BufferedImage detectedFaces = null;
            int count;
            if (SettingsView.selectedAlgorithm.equalsIgnoreCase("Haar Cascade")) {
                detectedFaces = camera.detectFaces();
                count = camera.getFacesCount();
            } else {
                SkinColorDetection skinColorDetector = new SkinColorDetection();
                skinColorDetector.setOriginalImage(camera.getImageToAnalyze());
                Mat mask = skinColorDetector.detectSkinColor();
                skinColorDetector.detectFaces(mask, 50, 50, 150, 150);
                Mat orig = skinColorDetector.getOriginalImage();
                try {
                    detectedFaces = skinColorDetector.Mat2BufferedImage(orig);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                count = skinColorDetector.getDetectedFaces();
            }

            if (count > 0) {
                questionTextField.setEnabled(true);
                Span faceFound = new Span("I found a face!");
                faceFound.setId("facefound-text");
                cameraPopUp.add(faceFound);
                String responseH4 = "ChatBot: " + "Let's chat beautiful human!";
                conversation = responseH4 + "\n";
            } else {
                questionTextField.setEnabled(false);
                Span notFound = new Span("I couldn't find anyone!");
                notFound.setId("notfound-text");
                cameraPopUp.add(notFound);
                String responseH4 = "ChatBot: " + "Looks like nobody is there!";
                conversation = responseH4 + "\n";
            }
            area.setValue(conversation);
            StreamResource streamResource = camera.generateUiImage(detectedFaces);
            Image cameraPic = new Image(streamResource, "capture");
            cameraPic.setId("camera-frame");
            cameraPopUp.add(cameraPic);
            cameraPopUp.add(retakeImage);
            cameraPopUp.add(analyzeButton);
        });
    }

    private void snapshotButton(Button button) {
        button.addClickListener(e -> {
            cameraPopUp.removeAll();
            camera.turnOnCamera();
            BufferedImage cameraSnapshot = camera.captureImage();
            camera.closeCamera();
            StreamResource streamResource = camera.generateUiImage(cameraSnapshot);
            Image cameraPic = new Image(streamResource, "capture");
            cameraPic.setId("camera-frame");
            cameraPopUp.add(cameraPic);
            cameraPopUp.add(retakeImage);
            cameraPopUp.add(analyzeButton);
        });
    }

    public void backgroundCheck() {
        cameraCheck.addClickListener(e -> {
            int count = 0;
            camera.turnOnCamera();
            while (count < 1) {

                if (SettingsView.selectedAlgorithm.equalsIgnoreCase("Haar Cascade")) {
                    camera.captureImage();
                    camera.detectFaces();
                    count = camera.getFacesCount();

                } else {
                    camera.captureImage();
                    SkinColorDetection skinColorDetector = new SkinColorDetection();
                    skinColorDetector.setOriginalImage(camera.getImageToAnalyze());
                    Mat mask = skinColorDetector.detectSkinColor();
                    skinColorDetector.detectFaces(mask, 50, 50, 150, 150);
                    count = skinColorDetector.getDetectedFaces();
                }

            }
            camera.closeCamera();
            String responseH4 = "ChatBot: " + "I can see you now! Let's chat!";
            conversation = responseH4 + "\n";
            area.setValue(conversation);
            questionTextField.setEnabled(true);

        });


    }


}
