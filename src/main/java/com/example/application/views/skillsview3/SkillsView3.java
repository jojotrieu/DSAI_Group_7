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
import java.util.Set;

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
    private ArrayList<Boolean> varClickListener = new ArrayList<>();
    private Button removeLastQuestion = new Button("Remove last question");

    private String currentTemplate = new String();
    private ArrayList<Object> currentComponents = new ArrayList<>();
    private Label error;

    private ArrayList<Label> varLabels = new ArrayList<>();
    private ArrayList<ArrayList<TextField>> valuesOfVar = new ArrayList<>();
    private ArrayList<Button> valuesButton = new ArrayList<>();
    private Button backButton = new Button("Back");

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

    private void initAddButton() {
        addButton.setId("addSkill-button");
        addButton.addClickListener(e -> {
            templateEmpty();
            newTemplate.open();
        });
    }

    private void setUpTemplate() {
        newTemplate.setWidth("700px");

        currentTemplate = "questionTemplate";

        title.setId("title");
        TextField question = new TextField("Question(s):");
        question.setId("question");
        questions.add(question);
        Button variable = new Button("Variable");
        variable.setId("variable");
        variables.add(variable);
        varClickListener.add(false);

        currentComponents.add(removeLastQuestion);
        currentComponents.add(alternativeButton);
        currentComponents.add(nextButton);
        currentComponents.add(title);
        currentComponents.add(questions.get(0));
        currentComponents.add(variables.get(0));

        addComponentsToTemplate();

        removeLastQuestion.setId("removeLQ-button");
        alternativeButton.setId("alternative-button");
        nextButton.setId("next-button");
    }

    private void addComponentsToTemplate(){
        for (Object obj : currentComponents){
            newTemplate.add((Component) obj);
        }
    }

    private void templateEmpty() {
        newTemplate.removeAll();
        title.setValue("");
        for(TextField q : questions){
            q.setValue("");
        }
        currentTemplate = "questionTemplate";
        varLabels.clear();
        valuesOfVar.clear();
        valuesButton.clear();

        currentComponents.clear();
        currentComponents.add(removeLastQuestion);
        currentComponents.add(alternativeButton);
        currentComponents.add(nextButton);
        currentComponents.add(title);
        currentComponents.add(questions.get(0));
        currentComponents.add(variables.get(0));
        addComponentsToTemplate();
    }

    private void initRemoveLQ(){
        removeLastQuestion.addClickListener(e -> {
            if(questions.size()>1){
                if (currentComponents.contains(error)){
                    currentComponents.remove(error);
                    newTemplate.remove(error);
                }

                currentComponents.remove(questions.get(questions.size()-1));
                currentComponents.remove(variables.get(variables.size()-1));
                newTemplate.remove(questions.get(questions.size()-1),variables.get(variables.size()-1));
                questions.remove(questions.get(questions.size()-1));
                variables.remove(variables.get(variables.size()-1));
                varClickListener.remove(varClickListener.get(varClickListener.size()-1));
            }
        });
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
                varClickListener.add(false);
                initVarButton();
                currentComponents.add(questions.get(questions.size()-1));
                currentComponents.add(variables.get(variables.size()-1));
                newTemplate.add(questions.get(questions.size()-1));
                newTemplate.add(variables.get(variables.size()-1));

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
                if(SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty()){
                    goToVarTemplate();

                }else if (!SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty()){
                    //display error message
                    error = new Label(SyntaxHandler.getErrorMessage());
                    error.setId("error-message");
                    currentComponents.add(error);
                    newTemplate.add(error);
                }else{
                    //display error message
                    error = new Label("Title must not be empty");
                    error.setId("error-message");
                    newTemplate.add(error);
                }

            }else if(currentTemplate.equals("variableTemplate")){
                currentComponents.clear();
                newTemplate.removeAll();

            }else if(currentTemplate.equals("answerTemplate")){
                currentComponents.clear();
                newTemplate.removeAll();
            }
        });
    }

    private void goToVarTemplate(){
        currentTemplate = "variableTemplate";
        currentComponents.clear();
        newTemplate.removeAll();
        backButton.setId("back-button");
        currentComponents.add(backButton);
        currentComponents.add(nextButton);

        initNextButton();
        initBackButton();

        Set<String> var = SyntaxHandler.getVariables(0);

        for(String v : var){
            currentComponents.add(new HtmlComponent("br"));
            Label label = new Label(v);
            varLabels.add(label);
            currentComponents.add(label);
            ArrayList<TextField> arrayOfValues = new ArrayList<>();
            TextField value = new TextField();
            value.setId("value-txtfield");
            currentComponents.add(value);
            arrayOfValues.add(value);
            valuesOfVar.add(arrayOfValues);
            Button addValue = new Button("Add value");
            addValue.setId("addVal-button");
            currentComponents.add(addValue);
            valuesButton.add(addValue);

            addComponentsToTemplate();

            // ArrayList<ArrayList<TextField>> valuesOfVar = new ArrayList<>();
            // List of size of amount of variable < list of every value for one variable >

            addValue.addClickListener(ev ->{
                int indexOfv = getIndex(var, v);

                // button "back", "next", breakline for every new var
                int index = 2 + valuesOfVar.size();
                // add var-value-add or br-value-br
                for(int i = 0; i<=indexOfv ; i++){
                    index += 3*valuesOfVar.get(i).size(); }

                TextField val = new TextField();
                valuesOfVar.get(indexOfv).add(val);
                currentComponents.add(index , new HtmlComponent("br"));
                currentComponents.add(index + 1 , val);
                currentComponents.add(index , new HtmlComponent("br"));
                newTemplate.removeAll();
                addComponentsToTemplate();
            });
        }
    }

    // returns index of an element of a Set
    private static int getIndex(Set set, Object value) {
        int index = 0;
        for (Object entry:set) {
            if (entry.equals(value)) return index;
            index++;
        }
        return -1;
    }

    private void initBackButton() {
        backButton.addClickListener(e -> {

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

            }else if(currentTemplate.equals("answerTemplate")){
                currentTemplate = "variableTemplate";
                newTemplate.removeAll();
            }
        });
    }

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
}
