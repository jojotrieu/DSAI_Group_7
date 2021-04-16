package com.example.application.views.skillsview2;

import com.example.application.services.chatbot.Action;
import com.example.application.services.chatbot.CFG;
import com.example.application.services.chatbot.Rule;
import com.example.application.services.chatbot.Skills;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "skills2", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations2.css")
@PageTitle("Skills Editor 2")
public class SkillsView2 extends Div {

    private Grid<Rule> rulesGrid = new Grid<>(Rule.class);
    private ArrayList<Rule> rules = CFG.rules;
    private Dialog newTemplate1 = new Dialog();

    private Button createButton1 = new Button("New");
    private Button deleteButton1 = new Button("Delete");
    private Button editButton1 = new Button("Edit");

    private TextField variable1 = new TextField("Variable");
    private TextField expression1 = new TextField("Expression");
    private Button plusExpr1 = new Button("+");
    private int pressed = 0;
    private TextField additionalExpr = new TextField("Expression");
    private TextField additionalExprBis = new TextField("Expression");
    private Button saveButton1 = new Button("Save");

    private Grid<Action> actionsGrid = new Grid<>(Action.class);
    private ArrayList<Action> actions = Skills.actions;
    private Dialog newTemplate2 = new Dialog();

    private Button createButton2 = new Button("New");
    private Button deleteButton2 = new Button("Delete");
    private Button editButton2 = new Button("Edit");

    private TextField variable2 = new TextField("Variable");
    private TextField expression2 = new TextField("Expression");
    private Button saveButton2 = new Button("Save");

    public SkillsView2() {
        setId("configurations2-view");
        CFG.loadRules();
        Skills.loadActions();
        initGrid1();
        initGrid2();
        setUpTemplates();
        initCreateButtons();
        initDeleteButtons();
        initEditButtons();
    }

    private void initCreateButtons() {
        createButton1.setId("create-button");
        createButton1.addClickListener(e -> {
            template1Empty();
            newTemplate1.open();
        });

        createButton2.setId("create-button");
        createButton2.addClickListener(e -> {
            template2Empty();
            newTemplate2.open();
        });
    }

    private void initDeleteButtons() {

        deleteButton1.setId("delete-button");
        deleteButton1.addClickListener(e -> {
            Object[] tempSet = rulesGrid.getSelectedItems().toArray();
            System.out.println(tempSet[0].toString());
            String deleteQuestion = tempSet[0].toString();
            //TODO delete from table
            //TODO remove from file
            rulesGrid.deselectAll();
            rulesGrid.setItems(rules);
        });

        deleteButton2.setId("delete-button");
        deleteButton2.addClickListener(e -> {
            Object[] tempSet = rulesGrid.getSelectedItems().toArray();
            System.out.println(tempSet[0].toString());
            String deleteQuestion = tempSet[0].toString();
            //TODO delete from table
            //TODO remove from file
            actionsGrid.deselectAll();
            actionsGrid.setItems(actions);
        });
    }

    private void initEditButtons() {
        editButton1.addClickListener(e -> {

        });

        editButton2.addClickListener(e -> {

        });
    }

    private void initGrid1() {
        rulesGrid.setItems(rules);
        //rulesGrid.setColumns("id", "variables", "expressions");
        add(rulesGrid);
        add(createButton1, deleteButton1, editButton1);
    }

    private void initGrid2() {
        actionsGrid.setItems(actions);
        //actionsGrid.setColumns("id", "variables", "expressions");
        add(actionsGrid);
        add(createButton2, deleteButton2, editButton2);
    }

    private void setUpTemplates(){
        variable1.setWidth("500px");
        expression1.setWidth("500px");
        newTemplate1.setWidth("700px");
        newTemplate1.setHeight("700");
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
        newTemplate1.add(plusExpr1);
        newTemplate1.add(saveButton1);

        variable2.setWidth("500px");
        expression2.setWidth("500px");
        newTemplate2.setWidth("700px");
        newTemplate2.setHeight("700");
        newTemplate2.add(variable2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);

        plusExpr1.addClickListener(e -> {
            pressed++;
            if(pressed==1){
                newTemplate1.add(additionalExpr);
            }else if(pressed==2){
                newTemplate1.add(additionalExprBis);
            }
        });

        saveButton1.addClickListener(e -> {
            Rule rule = new Rule();

            List<String> expressions = new ArrayList<>();
            expressions.add(expression1.getValue());
            expressions.add(additionalExpr.getValue());
            expressions.add(additionalExprBis.getValue());

            rule.setVariable(variable1.getValue());
            rule.setId(rules.size()+1);
            rule.setExpressions(expressions);

            CFG.addRule(rule);
            try {
                CFG.writeRules();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            CFG.loadRules();
            rulesGrid.setItems(rules);

            template1Empty();
            newTemplate1.close();
        });

        saveButton2.addClickListener(e -> {
            Action action = new Action();

            action.setId(actions.size()+1);
            action.setVariable(variable2.getValue());
            action.setExpression(expression2.getValue());

            Skills.addAction(action);
            try {
                Skills.writeActions();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            Skills.loadActions();
            actionsGrid.setItems(actions);

            template2Empty();
            newTemplate2.close();
        });
    }

    private void template2Empty() {
        variable2.setValue("");
        expression2.setValue("");
        newTemplate2.removeAll();
        newTemplate2.add(variable2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);
    }

    private void template1Empty() {
        pressed = 0;
        variable1.setValue("");
        expression1.setValue("");
        newTemplate1.removeAll();
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
        newTemplate1.add(plusExpr1);
        newTemplate1.add(saveButton1);
    }
}