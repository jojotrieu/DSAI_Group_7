package com.example.application.views.skillsview3;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Route(value = "skills3", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations3.css")
@PageTitle("Skills Editor")
public class SkillsView3 extends Div {

    // elements of main page
    private Button addButton = new Button("Add skill");
    private Dialog newTemplate = new Dialog();

    // elements of dialog box when currentTemplate = questionTemplate
    private TextField title = new TextField("Name:");
    private ArrayList<TextField> questions = new ArrayList<>();
    private ArrayList<Button> variables = new ArrayList<>();
    private Button alternativeButton = new Button("Alternative question");
    private Button nextButton = new Button("Next");
    private ArrayList<Boolean> varClickListener = new ArrayList<>();
    private Button removeLastQuestion = new Button("Remove last question");

    private String currentTemplate = new String();
    private Label error;

    // elements of dialog box when currentTemplate = variablesTemplate
    private ArrayList<Label> varLabels = new ArrayList<>();
    ArrayList<TextField> values = new ArrayList<>();
    private ArrayList<Button> valuesButton = new ArrayList<>();
    private Button backButton = new Button("Back");
    private ArrayList<Button> variables2 = new ArrayList<>();
    private ArrayList<Boolean> varClickListener2 = new ArrayList<>();

    public SkillsView3() {
        setId("configurations3-view");
        add(addButton);
        setUpTemplate();
        initAddButton();
        initAlternativeButton();
        initNextButton();
        initVarButton();
        initRemoveLQ();
    }

    /**
     * Initialize the button "add"
     * When button is pressed, dialog box open
     */
    private void initAddButton() {
        addButton.setId("addSkill-button");
        addButton.addClickListener(e -> {
            templateEmpty();
            newTemplate.open();
        });
    }

    /**
     * Setting up the initial template
     * currentTemplate = questionTemplate
     */
    private void setUpTemplate() {
        newTemplate.setWidth("700px");

        currentTemplate = "questionTemplate";

        TextField question = new TextField("Question(s):");
        questions.add(question);
        Button variable = new Button("Variable");
        variables.add(variable);
        varClickListener.add(false);

        // add components to the template
        newTemplate.add(removeLastQuestion);
        newTemplate.add(alternativeButton);
        newTemplate.add(nextButton);
        newTemplate.add(title);
        newTemplate.add(questions.get(0));
        newTemplate.add(variables.get(0));

        // set ids
        variable.setId("variable");
        question.setId("question");
        title.setId("title");
        removeLastQuestion.setId("removeLQ-button");
        alternativeButton.setId("alternative-button");
        nextButton.setId("next-button");
    }

    /**
     * Clean the template and set it to the initial template (questionTemplate)
     */
    private void templateEmpty() {
        newTemplate.removeAll();
        title.setValue("");
        for(TextField q : questions){
            q.setValue("");
        }
        currentTemplate = "questionTemplate";
        varLabels.clear();
        values.clear();
        valuesButton.clear();

        newTemplate.removeAll();
        newTemplate.add(removeLastQuestion);
        newTemplate.add(alternativeButton);
        newTemplate.add(nextButton);
        newTemplate.add(title);
        newTemplate.add(questions.get(0));
        newTemplate.add(variables.get(0));
    }

    /**
     * Initialize the button "remove last question"
     * When the button is pressed, the last question textfield is removed
     * as well as the last "var" button
     */
    private void initRemoveLQ(){
        removeLastQuestion.addClickListener(e -> {
            if(questions.size()>1){

                newTemplate.remove(questions.get(questions.size()-1),variables.get(variables.size()-1));
                questions.remove(questions.get(questions.size()-1));
                variables.remove(variables.get(variables.size()-1));
                varClickListener.remove(varClickListener.get(varClickListener.size()-1));
            }
        });
    }

    /**
     * Initialize the button "alternative"
     * The button is used differently depending on the currentTemplate value
     */
    private void initAlternativeButton() {
        alternativeButton.addClickListener(e -> {

            // when currentTemplate = questionTemplate: add an alternative question
            if(currentTemplate.equals("questionTemplate")){
                TextField question = new TextField();
                question.setId("question");
                questions.add(question);
                Button variable = new Button("Variable");
                variable.setId("variable-button");
                variables.add(variable);
                varClickListener.add(false);
                initVarButton();
                newTemplate.add(questions.get(questions.size()-1));
                newTemplate.add(variables.get(variables.size()-1));

            // when currentTemplate = answerTemplate: add an alternative answer
            }else if(currentTemplate.equals("answerTemplate")){

            }
        });
    }

    /**
     * Initialize the button "next"
     * When the button is pressed, go to next template
     */
    private void initNextButton() {
        nextButton.addClickListener(e -> {

            // when currentTemplate = questionTemplate: go to variableTemplate
            if(currentTemplate.equals("questionTemplate")){

                ArrayList<String> arrayQuestions = new ArrayList<>();
                for(TextField question : questions){
                    arrayQuestions.add(question.getValue());
                }

                if(SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty()){
                    goToVarTemplate();
                }else if (!SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty()){
                    //display error message
                    error = new Label(SyntaxHandler.getErrorMessage());
                    error.setId("error-message");
                    newTemplate.add(error);
                }else{
                    //display error message
                    error = new Label("Title must not be empty");
                    error.setId("error-message");
                    newTemplate.add(error);
                }

            // when currentTemplate = variableTemplate: go to answerTemplate
            }else if(currentTemplate.equals("variableTemplate")){

                int sizeOfVarNonEditable = values.size();
                int i = 0;
                for(TextField v : values){
                    System.out.println(v.getValue());
                    valuesButton.get(i).setEnabled(false);
                    variables2.get(i).setEnabled(false);
                    v.setReadOnly(true);
                    i++;
                }

                // Set<String> variablesInValues = SyntaxHandler.getVariablesInValues(1);
                Set<String> variablesInValues = new HashSet<>();
                variablesInValues.add("<A>");variablesInValues.add("<BC>");variablesInValues.add("<DEF>");

                for(String v : variablesInValues){
                    newTemplate.add(new HtmlComponent("br"));
                    Label label = new Label(v);
                    varLabels.add(label);
                    newTemplate.add(label);
                    TextField value = new TextField();
                    values.add(value);
                    value.setId("value-txtfield");
                    newTemplate.add(value);
                    Button varButt = new Button("Variable");
                    varButt.setId("variable-button");
                    variables2.add(varButt);
                    varClickListener2.add(false);
                    newTemplate.add(varButt);
                    initVarButton2();
                    Button addValue = new Button("Add value");
                    addValue.setId("addVal-button");
                    newTemplate.add(addValue);
                    valuesButton.add(addValue);

                    // add "," between every value of the variable
                    addValue.addClickListener(ev ->{
                        int indexOfvar = getIndex(variablesInValues, v);
                        values.get(indexOfvar+sizeOfVarNonEditable).setValue(values.get(indexOfvar+sizeOfVarNonEditable).getValue() + ",");
                    });
                }

            // when currentTemplate = answerTemplate: save skill
            }else if(currentTemplate.equals("answerTemplate")){
                nextButton.setText("SAVE SKILL");
                newTemplate.close();
            }
        });
    }

    /**
     * Go to the template variableTemplate
     */
    private void goToVarTemplate(){
        currentTemplate = "variableTemplate";
        newTemplate.removeAll();
        backButton.setId("back-button");
        newTemplate.add(backButton);
        newTemplate.add(nextButton);

        Set<String> var = SyntaxHandler.getVariables(0);

        for(String v : var){
            newTemplate.add(new HtmlComponent("br"));
            Label label = new Label(v);
            varLabels.add(label);
            newTemplate.add(label);
            TextField value = new TextField();
            values.add(value);
            value.setId("value-txtfield");
            newTemplate.add(value);
            Button varButt = new Button("Variable");
            varButt.setId("variable-button");
            variables2.add(varButt);
            varClickListener2.add(false);
            newTemplate.add(varButt);
            initVarButton2();
            Button addValue = new Button("Add value");
            addValue.setId("addVal-button");
            newTemplate.add(addValue);
            valuesButton.add(addValue);

            // add "," between every value of the variable
            addValue.addClickListener(ev ->{
                int indexOfvar = getIndex(var, v);
                values.get(indexOfvar).setValue(values.get(indexOfvar).getValue() + ",");
            });
        }
    }

    /**
     * returns index of an element of a Set
     * @param set : the set
     * @param value : a value of the set
     * @return the index of the value in the Set
     */
    private static int getIndex(Set set, Object value) {
        int index = 0;
        for (Object entry:set) {
            if (entry.equals(value)) return index;
            index++;
        }
        return -1;
    }

    /**
     * Initialize the button "back" to go back from one template to another
     * The button works differently depending on the currentTemplate
     */
    private void initBackButton() {
        backButton.addClickListener(e -> {

            // when currentTemplate = variableTemplate, go to questionTemplate
            if(currentTemplate.equals("variableTemplate")){
                newTemplate.removeAll();
                currentTemplate = "questionTemplate";

                title.setValue(title.getValue());
                for(TextField q : questions){
                    q.setValue(q.getValue());
                }

                newTemplate.add(removeLastQuestion);
                newTemplate.add(alternativeButton);
                newTemplate.add(nextButton);
                newTemplate.add(title);
                for(int i=0; i<questions.size(); i++){
                    newTemplate.add(questions.get(i));
                    newTemplate.add(variables.get(i));
                }

            // when currentTemplate = answerTemplate, go to variableTemplate
            }else if(currentTemplate.equals("answerTemplate")){
                currentTemplate = "variableTemplate";
                newTemplate.removeAll();
            }
        });
    }

    /**
     * Initialize the button "var" (for questionTemplate)
     * When the button is pressed it inserts a variable into the question textfield
     */
    private void initVarButton() {
        for(Button varButt : variables){
            int index = variables.indexOf(varButt);
            if(!varClickListener.get(index)){
                varClickListener.set(index, true);
                varButt.addClickListener(e ->{
                    questions.get(index).setValue(questions.get(index).getValue() + "<...>");
                });
            }
        }
    }

    /**
     * Initialize the button "var" (for variableTemplate)
     * When the button is pressed it inserts a variable into the value textfield
     */
    private void initVarButton2() {
        for(Button varButt : variables2){
            int index = variables2.indexOf(varButt);
            if(!varClickListener2.get(index)){
                varClickListener2.set(index, true);
                varButt.addClickListener(e ->{
                    values.get(index).setValue(values.get(index).getValue() + "<...>");
                });
            }
        }
    }
}