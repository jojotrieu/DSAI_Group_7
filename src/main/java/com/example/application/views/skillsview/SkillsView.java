package com.example.application.views.skillsview;

import com.example.application.services.phase1chatbot.Question;
import com.example.application.services.phase1chatbot.SkillParser;
import com.sun.xml.bind.v2.TODO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;
import org.json.simple.JSONObject;

import java.util.List;

@Route(value = "skills", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations.css")
@PageTitle("Skills Editor")
public class SkillsView extends Div {

    private Dialog newTemplate = new Dialog();
    private Button addTemplate = new Button("New");
    private Button deleteEntry = new Button("Delete");
    private Button editEntry = new Button("Edit");
    private Button submitTemplate = new Button("Submit");
    private Button mergeButton = new Button("Merge with other skills");

    private TextField request = new TextField("Request");
    private TextField slotOne = new TextField("Slot 1");
    private TextField slotTwo = new TextField("Slot 2");
    private TextField response = new TextField("Response");
    private MemoryBuffer buffer = new MemoryBuffer();
    private Upload upload = new Upload(buffer);
    private Div output = new Div();
    private Paragraph errorText = new Paragraph();

    private TreeGrid<String> grid = new TreeGrid<>();
    private SkillParser jsonFile = new SkillParser();


    public SkillsView() {
        setId("configurations-view");
        initDialog();
        initDeleteButton();
        initUpload();
        initGrid();
        add(new H4("Templates Editor"));
        add(grid);
        add(addTemplate, deleteEntry, editEntry);
        add(upload, output);
    }

    private void initGrid() {

    }

    private void initDialog() {
        request.setWidth("500px");
        response.setWidth("500px");
        slotOne.setWidth("200px");
        slotTwo.setWidth("200px");
        slotTwo.setId("slot-two");
        newTemplate.setWidth("550px");
        newTemplate.setHeight("450");
        newTemplate.add(request);
        newTemplate.add(slotOne);
        newTemplate.add(slotTwo);
        newTemplate.add(response);
        newTemplate.add(submitTemplate);
        request.setId("request-textfield");
        response.setId("response-textfield");
        addTemplate.setId("add-button");
        editEntry.setId("edit-button");
        editEntry.setEnabled(false);
        addTemplate.addClickListener(e -> {
            newTemplate.open();
        });
        submitTemplate.addClickListener(e -> {
            String requestString = request.getValue();
            String responseString = response.getValue();
            String slotOneString = slotOne.getValue();
            String slotTwoString = slotTwo.getValue();
            boolean error = requestString.isEmpty() || responseString.isEmpty() ||
                    slotOneString.isEmpty() || slotTwoString.isEmpty();
            if (!error) {
                Question newQuestion = new Question(requestString);
                List<String> properties = newQuestion.getPropertiesList();
                jsonFile.newSkill(newQuestion);
                JSONObject actionConditions = new JSONObject();
                actionConditions.put(properties.get(0), slotOneString);
                actionConditions.put(properties.get(1), slotTwoString);
                jsonFile.addAction(newQuestion, responseString, actionConditions);
            } else {
                //TODO: Add error window here(not all fields have text)
            }
            newTemplate.close();
        });
    }

    private void initDeleteButton() {
        deleteEntry.setId("delete-button");
        deleteEntry.setEnabled(false);
    }

    private void initUpload() {
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label("Upload a max. 10KB file in .txt or .csv format"));
        upload.setAcceptedFileTypes("text/csv", ".txt");
        upload.setMaxFileSize(10000);
        upload.setUploadButton(mergeButton);
        upload.addFileRejectedListener(event -> {
            errorText.setText(event.getErrorMessage());
            output.add(errorText);
        });
        upload.addSucceededListener(event -> {
            output.remove(errorText);
        });
    }
}
