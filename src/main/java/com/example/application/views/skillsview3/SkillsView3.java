package com.example.application.views.skillsview3;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
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
    private Button alternativeButton = new Button("Alternative question");
    private Button nextButton = new Button("Next");

    private String currentTemplate = new String();

    public SkillsView3() {
        setId("configurations3-view");
        add(addButton);
        setUpTemplate();
        initAddButton();
        initAlternativeButton();
        initNextButton();
    }

    private void initAddButton() {
        addButton.setId("addSkill-button");
        addButton.addClickListener(e -> {
            templateEmpty();
            newTemplate.open();
        });
    }

    private void setUpTemplate() {
        newTemplate.setWidth("750px");

        currentTemplate = "questionTemplate";

        title.setId("title");
        TextField question = new TextField("Question(s):");
        question.setId("question");
        questions.add(question);
        Button variable = new Button("Variable");
        variable.setId("variable");
        variables.add(variable);

        newTemplate.add(alternativeButton);
        newTemplate.add(nextButton);
        newTemplate.add(title);
        newTemplate.add(questions.get(0));
        newTemplate.add(variables.get(0));
        alternativeButton.setId("alternative-button");
        nextButton.setId("next-button");
    }

    private void templateEmpty() {
        title.setValue("");
        for(TextField q : questions){
            q.setValue("");
        }
        newTemplate.add(alternativeButton);
        newTemplate.add(nextButton);
        newTemplate.add(title);
        newTemplate.add(questions.get(0));
        newTemplate.add(variables.get(0));
    }

    private void initAlternativeButton() {
        alternativeButton.addClickListener(e -> {

            if(currentTemplate.equals("questionTemplate")){
                TextField question = new TextField();
                question.setId("question");
                questions.add(question);
                Button variable = new Button("Variable");
                variable.setId("variable-button");
                variables.add(variable);
                newTemplate.add(questions.get(questions.size()-1));
                newTemplate.add(variables.get(variables.size()-1));

            }else if(currentTemplate.equals("variableTemplate")){


            }else if(currentTemplate.equals("answerTemplate")){

            }
        });
    }

    private void initNextButton() {
        nextButton.addClickListener(e -> {

            if(currentTemplate.equals("questionTemplate")){

                ArrayList<String> arrayQuestions = new ArrayList<>();
                for(TextField question : questions){
                    arrayQuestions.add(question.getValue());
                }
                Label error = new Label("error");
                error.setId("error-message");
                newTemplate.add(error);
                /*if(SyntaxHandler.checkQuestions(arrayQuestions)){
                    newTemplate.removeAll();
                    // add elements of next template
                }else{
                    //display error message
                    Label error = new Label(SyntaxHandler.getErrorMessage());
                    error.setId("error-message");
                    newTemplate.add(error);
                }*/

            }else if(currentTemplate.equals("variableTemplate")){
                newTemplate.removeAll();

            }else if(currentTemplate.equals("answerTemplate")){
                newTemplate.removeAll();

            }
        });
    }

}
