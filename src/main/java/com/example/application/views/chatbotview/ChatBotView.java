package com.example.application.views.chatbotview;

import com.example.application.services.ChatBot;
import com.example.application.services.camera.Camera;
import com.example.application.views.main.MainView;
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

import java.awt.image.BufferedImage;


@Route(value = "chatbot", layout = MainView.class)
@PageTitle("ChatBot")
@CssImport("./styles/views/chatbot/chatbot.css")
@RouteAlias(value = "", layout = MainView.class)
public class ChatBotView extends HorizontalLayout {

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

        cameraCheck.addClickListener(e -> {
            cameraPopUp.removeAll();
            snapshot.setId("snapshot-button");
            cameraPopUp.add(snapshot);
            cameraPopUp.open();
        });

        snapshot.addClickListener(e -> {
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

        retakeImage.addClickListener(e -> {
            cameraPopUp.removeAll();
            BufferedImage cameraSnapshot = camera.captureImage();
            StreamResource streamResource = camera.generateUiImage(cameraSnapshot);
            Image cameraPic = new Image(streamResource, "capture");
            cameraPic.setId("camera-frame");
            cameraPopUp.add(cameraPic);
            cameraPopUp.add(retakeImage);
            cameraPopUp.add(analyzeButton);
        });

        analyzeButton.addClickListener(e -> {
            cameraPopUp.removeAll();
            BufferedImage detectedFaces = camera.detectFaces();
            int count = camera.getFacesCount();
            if (count > 0) {
                questionTextField.setEnabled(true);
                Span faceFound = new Span("I found a face!");
                faceFound.setId("facefound-text");
                cameraPopUp.add(faceFound);
                String responseH4 = "ChatBot: " + "Let's chat beautiful human!";
                conversation = responseH4 + "\n";
                area.setValue(conversation);
            } else {
                questionTextField.setEnabled(false);
                Span notFound = new Span("I couldn't find anyone!");
                notFound.setId("notfound-text");
                cameraPopUp.add(notFound);
                String responseH4 = "ChatBot: " + "Looks like nobody is there!";
                conversation = responseH4 + "\n";
                area.setValue(conversation);
            }
            StreamResource streamResource = camera.generateUiImage(detectedFaces);
            Image cameraPic = new Image(streamResource, "capture");
            cameraPic.setId("camera-frame");
            cameraPopUp.add(cameraPic);
            cameraPopUp.add(retakeImage);
            cameraPopUp.add(analyzeButton);
        });


    }


}
