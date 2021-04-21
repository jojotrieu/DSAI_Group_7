package com.example.application.views.skillsview2;

import com.example.application.services.chatbot.*;

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
    private List<Rule> rules = CFG.getRules();
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
    private boolean editPressed1 = false;
    private String selection1;

    private Grid<Action> actionsGrid = new Grid<>(Action.class);
    private ArrayList<Action> actions = Skills.actions;
    private Dialog newTemplate2 = new Dialog();

    private Button createButton2 = new Button("New");
    private Button deleteButton2 = new Button("Delete");
    private Button editButton2 = new Button("Edit");

    private TextField variable2 = new TextField("Variable");
    private TextField expression2 = new TextField("Expression");
    private Button saveButton2 = new Button("Save");
    private boolean editPressed2 = false;
    private String selection2;

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
            editPressed1 = false;
            template1Empty();
            newTemplate1.open();
        });

        createButton2.setId("create-button");
        createButton2.addClickListener(e -> {
            editPressed2 = false;
            template2Empty();
            newTemplate2.open();
        });
    }

    private void initDeleteButtons() {

        deleteButton1.setId("delete-button");
        deleteButton1.addClickListener(e -> {
            Object[] tempSet = rulesGrid.getSelectedItems().toArray();
            String deleteRule = tempSet[0].toString();

            removeRule(deleteRule);

            rulesGrid.deselectAll();
            rulesGrid.setItems(rules);
            try {
                CFG.writeRules();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        deleteButton2.setId("delete-button");
        deleteButton2.addClickListener(e -> {
            Object[] tempSet = actionsGrid.getSelectedItems().toArray();
            String deleteAction = tempSet[0].toString();

            removeAction(deleteAction);

            actionsGrid.deselectAll();
            actionsGrid.setItems(actions);
            try {
                Skills.writeActions();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
    }

    private void initEditButtons() {
        editButton1.addClickListener(e -> {
            if (rulesGrid.getSelectedItems().toArray().length != 0) {
                editPressed1 = true;
                newTemplate1.open();

                Object[] tempSet = rulesGrid.getSelectedItems().toArray();
                selection1 = tempSet[0].toString();
                Rule selectedRule = new Rule();

                for (Rule rule : rules) {
                    if (rule.toString().equals(selection1)) {
                        selectedRule.setId(rule.getId());
                        selectedRule.setVariable(rule.getVariable());
                        selectedRule.setExpressions(rule.getExpressions());
                        break;
                    }
                }

                variable1.setValue(selectedRule.getVariable());
                expression1.setValue(selectedRule.getExpressions().get(0));
                if(selectedRule.getExpressions().get(1).length() > 0){
                    pressed = 1;
                    newTemplate1.add(additionalExpr);
                    additionalExpr.setValue(selectedRule.getExpressions().get(1));
                }
                if(selectedRule.getExpressions().get(2).length() > 0){
                    pressed = 2;
                    newTemplate1.add(additionalExprBis);
                    additionalExprBis.setValue(selectedRule.getExpressions().get(2));
                }
            }
        });

        editButton2.addClickListener(e -> {

            if (actionsGrid.getSelectedItems().toArray().length != 0) {
                editPressed2 = true;
                newTemplate2.open();

                Object[] tempSet = actionsGrid.getSelectedItems().toArray();
                selection2 = tempSet[0].toString();
                Action selectedAction = new Action();

                for (Action action : actions) {
                    if (action.toString().equals(selection2)) {
                        selectedAction.setId(action.getId());
                        selectedAction.setVariable(action.getVariable());
                        selectedAction.setExpression(action.getExpression());
                        break;
                    }
                }

                variable2.setValue(selectedAction.getVariable());
                expression2.setValue(selectedAction.getExpression());
            }
        });
    }

    private void initGrid1() {
        //TODO add title to grid
        rulesGrid.setItems(rules);
        rulesGrid.removeAllColumns();
        rulesGrid.addColumn("id").setHeader("RULES");
        rulesGrid.addColumn("variable");
        rulesGrid.addColumn("expressions");
        add(rulesGrid);
        add(createButton1, deleteButton1, editButton1);
    }

    private void initGrid2() {
        //TODO add title to grid
        actionsGrid.setItems(actions);
        actionsGrid.removeAllColumns();
        actionsGrid.addColumn("id").setHeader("ACTIONS");
        actionsGrid.addColumn("variable");
        actionsGrid.addColumn("expression");
        add(actionsGrid);
        add(createButton2, deleteButton2, editButton2);
    }

    private void setUpTemplates() {
        variable1.setWidth("500px");
        expression1.setWidth("500px");
        newTemplate1.setWidth("700px");
        newTemplate1.setHeight("700");
        additionalExpr.setWidth("500px");
        additionalExprBis.setWidth("500px");
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
        newTemplate1.add(plusExpr1);
        newTemplate1.add(saveButton1);
        plusExpr1.setId("plus-expr-button");
        saveButton1.setId("save-button-1");

        variable2.setWidth("500px");
        expression2.setWidth("500px");
        newTemplate2.setWidth("700px");
        newTemplate2.setHeight("700");
        newTemplate2.add(variable2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);
        saveButton2.setId("save-button-2");

        plusExpr1.addClickListener(e -> {
            pressed++;
            if (pressed == 1) {
                newTemplate1.remove(saveButton1);
                newTemplate1.add(additionalExpr);
                newTemplate1.add(saveButton1);
                saveButton1.setId("save-button-1");
            } else if (pressed == 2) {
                newTemplate1.remove(saveButton1);
                newTemplate1.add(additionalExprBis);
                newTemplate1.add(saveButton1);
                saveButton1.setId("save-button-1");
            }
        });

        saveButton1.addClickListener(e -> {
            if(!error1()){
                System.out.println(error1());
                Rule rule = new Rule();

                List<String> expressions = new ArrayList<>();
                expressions.add(expression1.getValue());
                expressions.add(additionalExpr.getValue());
                expressions.add(additionalExprBis.getValue());

                rule.setVariable(variable1.getValue());
                rule.setId(rules.size() + 1);
                rule.setExpressions(expressions);

                CFG.addRule(rule);
                try {
                    CFG.writeRules();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                CFG.loadRules();
                rulesGrid.setItems(rules);

                if (editPressed1) {
                    removeRule(selection1);
                    rulesGrid.deselectAll();
                    rulesGrid.setItems(rules);
                    try {
                        CFG.writeRules();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }
                editPressed1 = false;
                selection1 = "";

                template1Empty();
                newTemplate1.close();
            }
            else {
                variable1.setId("variable1-error");
            }
        });

        saveButton2.addClickListener(e -> {
            if (!error2()) {
                Action action = new Action();

                action.setId(actions.size() + 1);
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

                if (editPressed2) {
                    removeAction(selection2);
                    actionsGrid.deselectAll();
                    actionsGrid.setItems(actions);
                    try {
                        Skills.writeActions();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }

                editPressed2 = false;
                selection2 = "";

                template2Empty();
                newTemplate2.close();

            } else {
                variable2.setId("variable2-error");
            }
        });
    }

    private void template2Empty() {
        variable2.setValue("");
        expression2.setValue("");
        newTemplate2.removeAll();
        newTemplate2.add(variable2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);
        variable2.setId("variable2");
    }

    private void template1Empty() {
        pressed = 0;
        variable1.setValue("");
        expression1.setValue("");
        additionalExpr.setValue("");
        additionalExprBis.setValue("");
        newTemplate1.removeAll();
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
        newTemplate1.add(plusExpr1);
        newTemplate1.add(saveButton1);
        variable1.setId("variable1");
    }

    private void removeRule(String deleteRule) {
        for (Rule rule : rules){
            if (rule.toString().equals(deleteRule)){
                CFG.removeRule(rule.getId());
                rules.remove(rule);
                break;
            }
        }
    }

    private void removeAction(String deleteAction) {
        for (Action action : actions){
            if (action.toString().equals(deleteAction)){
                Skills.removeAction(action.getId());
                actions.remove(action);
                break;
            }
        }
    }

    private boolean error1(){
        int len = variable1.getValue().length();

        if (variable1.getValue().charAt(0)=='<'){
            if(variable1.getValue().charAt(len-1)=='>'){
                variable1.getValue().toUpperCase();
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    private boolean error2(){
        int len = variable2.getValue().length();

        if (variable2.getValue().charAt(0)=='<'){
            if(variable2.getValue().charAt(len-1)=='>'){
                variable2.getValue().toUpperCase();
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }
}