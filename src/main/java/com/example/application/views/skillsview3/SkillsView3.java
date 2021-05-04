package com.example.application.views.skillsview3;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;

@Route(value = "skills3", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations3.css")
@PageTitle("Skills Editor")
public class SkillsView3 extends Div {

    private Button addButton = new Button("Add skill");
    private Dialog newTemplate = new Dialog();

    private TextField title = new TextField("Name:");
    private ArrayList<TextField> questions = new ArrayList<>();
    private ArrayList<Button> variables = new ArrayList<>();
    private Button alternativeButton1 = new Button("Alternative question");
    private Button nextButton1 = new Button("Next");

    public SkillsView3() {
        setId("configurations3-view");
        add(addButton);
        setUpTemplate();
        initAddButton();
    }

    private void setUpTemplate() {
        title.setWidth("500px");
        TextField question = new TextField("Question(s):");
        question.setWidth("500px");
        questions.add(question);
        Button variable = new Button("Variable");
        variables.add(variable);

        newTemplate.add(title);
        newTemplate.add(questions.get(0));
        newTemplate.add(variables.get(0));
        newTemplate.add(alternativeButton1);
        newTemplate.add(nextButton1);
    }

    private void initAddButton() {
        addButton.setId("addSkill-button");
        addButton.addClickListener(e -> {
            templateEmpty();
            newTemplate.open();
        });
    }

    private void templateEmpty() {
    }

}
