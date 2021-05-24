package com.example.application.views.skillsview3;

import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.CNF;
import com.example.application.services.chatbot.Rule;
import com.example.application.services.chatbot.Skills;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.stream.Collectors;

@Route(value = "skills3", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations3.css")
@PageTitle("Skills Editor")
public class SkillsView3 extends Div {

    // elements of main page
    private final Button addButton = new Button("Add skill");
    private final Dialog newTemplate = new Dialog();

    // common elements
    private String currentTemplate = "";
    private Label error;
    public static boolean noVar; // indicates whether the questions contains no variables or not
    public static int limit = 2;

    // elements of dialog box when currentTemplate = questionTemplate
    private final TextField title = new TextField("Name:");
    private final ArrayList<TextField> questions = new ArrayList<>();
    private final ArrayList<Button> variables = new ArrayList<>();
    private final Button alternativeButton = new Button("Alternative question");
    private final Button nextButton = new Button("Next");
    private final ArrayList<Boolean> varClickListener = new ArrayList<>();
    private final Button removeLastQuestion = new Button("Remove last question");

    // elements of dialog box when currentTemplate = variableTemplate
    private final ArrayList<Label> varLabels = new ArrayList<>();
    private final ArrayList<TextField> values = new ArrayList<>();
    private final ArrayList<Button> valuesButton = new ArrayList<>();
    private final Button backButton = new Button("Back");
    private final ArrayList<Button> variables2 = new ArrayList<>();
    private final ArrayList<Boolean> varClickListener2 = new ArrayList<>();

    // elements of dialog box when currentTemplate = answerTemplate
    private final ArrayList<TextField> answers = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> varVal = new ArrayList<>();
    private final ArrayList<Button> plusButt = new ArrayList<>();
    private final Button saveSkill = new Button("SAVE SKILL");

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
        newTemplate.setWidth("800px");

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
        title.setValue("<...>");
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
        title.setValue("<...>");
        for(TextField q : questions){
            q.setValue("");
        }
        currentTemplate = "questionTemplate";
        varLabels.clear();
        values.clear();
        valuesButton.clear();
        noVar = false;

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
     * as well as the last variable button
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
     * Once clicked, a new question textfield can be filled
     * Enables the user to ask a question in several ways
     */
    private void initAlternativeButton() {
        alternativeButton.addClickListener(e -> {
            TextField question = new TextField();
            question.setId("question");
            questions.add(question);
            Button variable = new Button("Variable");
            variable.setId("variable-button");
            variables.add(variable);
            varClickListener.add(false);
            initVarButton();
            newTemplate.add(questions.get(questions.size() - 1));
            newTemplate.add(variables.get(variables.size()-1));
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

                if(SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty() && isUnique(title.getValue())){
                    goToVarTemplate();
                }else if (!SyntaxHandler.checkQuestions(arrayQuestions) && !title.getValue().isEmpty()){
                    //display error message
                    error = new Label(SyntaxHandler.getErrorMessage());
                    error.setId("error-message");
                    newTemplate.add(new HtmlComponent("br"));
                    newTemplate.add(error);
                }else if(title.getValue().isEmpty()){
                    //display error message
                    error = new Label("Title must not be empty");
                    error.setId("error-message");
                    newTemplate.add(new HtmlComponent("br"));
                    newTemplate.add(error);
                }else{
                    error = new Label("Title must be unique");
                    error.setId("error-message");
                    newTemplate.add(new HtmlComponent("br"));
                    newTemplate.add(error);
                }

            // when currentTemplate = variableTemplate: go to answerTemplate
            }else if(currentTemplate.equals("variableTemplate")){
                ArrayList<String> allValues = getAllValues();
                SyntaxHandler.checkVariables(allValues, 1);
                Set<String> variablesInValues = SyntaxHandler.getVariables(1);

                if(!variablesInValues.isEmpty() && !limitInValues() && valuesFilled()) {

                    int sizeOfVarNonEditable = values.size();
                    int i = 0;
                    for (TextField v : values) {
                        if(valuesButton.get(i).isEnabled()) {
                            valuesButton.get(i).setEnabled(false);
                            variables2.get(i).setEnabled(false);
                            v.setReadOnly(true);
                            variablesInValues.remove(varLabels.get(i).getText());
                        }
                        i++;
                    }

                    for (String v : variablesInValues) {
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
                        addValue.addClickListener(ev -> {
                            int indexOfvar = getIndex(variablesInValues, v);
                            values.get(indexOfvar + sizeOfVarNonEditable).setValue(values.get(indexOfvar + sizeOfVarNonEditable).getValue() + ",");
                        });
                    }
                }else{
                    if(valuesFilled() && !limitInValues()){ // if all value textfields are filled
                        goToAnswerTemplate();
                    }else if(!valuesFilled()){
                        // display error if not all values of variables are filled
                        error = new Label("All values must be filled");
                        error.setId("error-message");
                        newTemplate.add(new HtmlComponent("br"));
                        newTemplate.add(error);
                    }else if(limitInValues()){
                        // display error if too many variables per value
                        error = new Label("Maximum " + limit + " variables per value");
                        error.setId("error-message");
                        newTemplate.add(new HtmlComponent("br"));
                        newTemplate.add(error);
                    }else{
                        error = new Label("All values must be filled & Maximum " + limit + " variables per value");
                        error.setId("error-message");
                        newTemplate.add(new HtmlComponent("br"));
                        newTemplate.add(error);
                    }
                }
            }
        });
    }

    /**
     * Go to the template variableTemplate
     * currentTemplate = variableTemplate
     * Displays elements of the variable template
     */
    private void goToVarTemplate(){
        currentTemplate = "variableTemplate";
        newTemplate.removeAll();
        backButton.setId("back-button");
        newTemplate.add(backButton);
        newTemplate.add(nextButton);

        Set<String> var = SyntaxHandler.getVariables(0);

        if(var.isEmpty()){ // when no variable in the question
            noVar = true;
        }

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
     * Go to the template of answers
     * currentTemplate = answerTemplate
     * Displays elements of the answer template
     */
    private void goToAnswerTemplate() {
        newTemplate.setWidth("1200px");
        HashMap<String,List<String>> hashmap = createHashMap();
        System.out.println(hashmap);
        List<Set<String>> set = SyntaxHandler.findCommonV(hashmap); // return common variables to put in answerTemplate
        System.out.println(set);

        currentTemplate = "answerTemplate";
        newTemplate.removeAll();

        newTemplate.add(saveSkill);
        initSaveSkillButton(hashmap);

        for (Set<String> se : set){
            List<String> list = new ArrayList<>(se);

            if(list.size()==1) {
                String s = list.get(0);
                List<String> values = hashmap.get(s);

                for (String str : values) {
                    if (!containsVar(str)) {
                        ArrayList<Object> vv = new ArrayList<>();
                        newTemplate.add(new HtmlComponent("br"));
                        Label labVar = new Label(s);
                        labVar.setId("variable-label-answer");
                        vv.add(labVar);
                        Label labVal = new Label(str);
                        labVar.setId("value-label-answer");
                        vv.add(labVal);
                        newTemplate.add(labVar);
                        newTemplate.add(labVal);
                        TextField ans = new TextField();
                        ans.setId("answer-textfield");
                        answers.add(ans);
                        newTemplate.add(ans);
                        varVal.add(vv);
                    }
                }
            }else { // combination of variables
                addCombination(list, hashmap);
            }
        }

        // even if there is no variable in the question, possibility to give an answer
        if(noVar){
            TextField ans = new TextField();
            ans.setId("answer-textfield");
            answers.add(ans);
            newTemplate.add(ans);
        }
    }

    private void addCombination(List<String> list, HashMap<String,List<String>> hashmap){
        newTemplate.add(new HtmlComponent("br"));
        ArrayList<Object> vv = new ArrayList<>();

        for (String element : list) {
            Label labVar = new Label(element);
            labVar.setId("variable-label-answer");
            vv.add(labVar);
            newTemplate.add(labVar);

            List<String> values = hashmap.get(element);
            ComboBox<String> comboBox = new ComboBox<String>("Select");
            comboBox.setItems(values);
            comboBox.setWidth("120px");
            vv.add(comboBox);
            newTemplate.add(comboBox);
        }
        varVal.add(vv);

        TextField ans = new TextField();
        ans.setId("answer-textfield");
        answers.add(ans);
        newTemplate.add(ans);

        Button plus = new Button("+");
        plus.setId("button-plus");
        plusButt.add(plus);
        newTemplate.add(plus);

        initPlusButton(plus, list, hashmap);
    }

    /**
     * TODO make it work
     * Initialize the button "back" to go back from one template to another
     * The button works differently depending on the currentTemplate
     */
    /*private void initBackButton() {
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
    }*/

    /**
     * Initialize the button "var" (for questionTemplate)
     * When the button is pressed it inserts a variable into the question textfield
     */
    private void initVarButton() {
        for(Button varButt : variables){
            int index = variables.indexOf(varButt);
            if(!varClickListener.get(index)){
                varClickListener.set(index, true);
                varButt.addClickListener(e ->
                        questions.get(index).setValue(questions.get(index).getValue() + "<...>")
                );
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
                varButt.addClickListener(e ->
                        values.get(index).setValue(values.get(index).getValue() + "<...>")
                );
            }
        }
    }

    private void initPlusButton(Button plus, List<String> list, HashMap<String,List<String>> hashmap){
        plus.addClickListener(e-> addCombination(list, hashmap));
    }

    /**
     * Initialize "SAVE SKILL3 button
     * Once pressed, close the template and save the skill into rules and actions
     * @param hashmap : the hashmap containing all needed strings to create the rules
     */
    private void initSaveSkillButton(HashMap<String, List<String>> hashmap) {
        saveSkill.addClickListener(e->{
            if(answersFilled()){
                SyntaxHandler.saveRules(hashmap);

                String t = title.getValue();
                List<String> a = textfieldToString(answers);
                SyntaxHandler.saveActions(t, a, varVal);

                CFG.loadRules();
                Skills.loadActions();
                CNF.initialize();

                newTemplate.close();
            }else{
                error = new Label("Answers' textfields must be filled in.");
                error.setId("error-message");
                newTemplate.add(new HtmlComponent("br"));
                newTemplate.add(error);
            }

        });
    }

    /**
     * Creates a hashmap containing the title of the skill, the questions, the variables and their values
     * @return hashmap
     */
    private HashMap<String,List<String>> createHashMap() {

        HashMap<String,List<String>> hashmap = new LinkedHashMap<>();

        List<String> arr = new ArrayList<>();

        for (TextField q : questions)  {
            arr.add(q.getValue());
        }

        hashmap.put(title.getValue(), arr);
        int i = 0;
        for(Label v : varLabels){
            String[] array = values.get(i).getValue().split(",");
            List<String> list = new ArrayList<>(Arrays.asList(array));
            hashmap.put(v.getText(), list);
            i++;
        }

        return hashmap;
    }

    /**
     * Transforms an ArrayList of textfields into a List of Strings
     * @param tf : the array of textfields
     * @return list: the List of Strings
     */
    private List<String> textfieldToString(ArrayList<TextField> tf) {
        return tf.stream().map(TextField::getValue).collect(Collectors.toList());
    }

    /**
     * Checks whether all answer textfields are filled
     * @return boolean: true if filled, else false
     */
    private boolean answersFilled() {
        return answers.stream().noneMatch(ans -> ans.getValue().equals(""));
    }

    /**
     * Checks whether all value textfields are filled
     * @return boolean: true if filled, else false
     */
    private boolean valuesFilled() {
        return values.stream().noneMatch(v -> v.getValue().equals(""));
    }

    /**
     * Checks whether all values of variables do not exceed the variable limit
     * @return boolean: true if it exceeds
     */
    private boolean limitInValues() {
        for(TextField v: values){
            String textfield = v.getValue();
            String[] val = textfield.split(",");
            for (String s : val) {
                if (SyntaxHandler.countVariables(s) > limit) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether there is a variable in the string
     * @param str the string which may contain a variable
     * @return true if there is a variable in the string, else false
     */
    private boolean containsVar(String str) {
        boolean returnval = false;

        String[] words = str.split(" ");
        for (String word : words) {
            if (CFG.isVariable(word)) {
                returnval = true;
            }
        }
        return returnval;

    }

    /**
     * returns index of an element of a Set
     * @param set : the set
     * @param value : a value of the set
     * @return the index of the value in the Set
     */
    private static int getIndex(Set<String> set, Object value) {
        int index = 0;
        for (Object entry : set) {
            if (entry.equals(value)) return index;
            index++;
        }
        return -1;
    }

    /**
     * Checks whether the title of the skill is unique
     * @param value the title
     * @return whether it is unique
     */
    private boolean isUnique(String value) {
        boolean res = false;

        for (Rule rule : CFG.rules) {
            if (rule.getVariable().equals("<ACTION>")) {
                for (String expr : rule.getExpressions()) {
                    if (expr.equals(value)) {
                        return false;
                    }
                }
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * Returns an array of string containing all values given to the variables
     */
    private ArrayList<String> getAllValues() {
        ArrayList<String> allValues = new ArrayList<>();
        values.stream().filter(tf -> !tf.isReadOnly()).map(tf -> tf.getValue().split(",")).map(Arrays::asList).forEach(allValues::addAll);
        return allValues;
    }
}