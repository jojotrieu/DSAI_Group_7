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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextField nonterminals2 = new TextField("Keys");
    private TextField expression2 = new TextField("Expression");
    private Button saveButton2 = new Button("Save");
    private boolean editPressed2 = false;
    private String selection2;

    public SkillsView2() {
        setId("configurations2-view");
        CFG.loadRules();
        Skills.loadActions();
        CNF.initialize();
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
                CFG.loadRules();
                CNF.initialize();
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
                Skills.loadActions();
                CNF.initialize();
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
                expression1.setValue(Rule.expressionToString(selectedRule.getExpressions()));
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
                        selectedAction.setNonTerminals(action.getNonTerminals());
                        selectedAction.setExpression(action.getExpression());
                        break;
                    }
                }

                variable2.setValue(selectedAction.getVariable());
                nonterminals2.setValue(selectedAction.getNonTerminalsToString());
                expression2.setValue(selectedAction.getExpression());
            }
        });
    }

    private void initGrid1() {
        rulesGrid.setItems(rules);
        rulesGrid.removeAllColumns();
        rulesGrid.addColumn("id").setHeader("RULES");
        rulesGrid.addColumn("variable");
        rulesGrid.addColumn("expressions");
        add(rulesGrid);
        add(createButton1, deleteButton1, editButton1);
    }

    private void initGrid2() {
        actionsGrid.setItems(actions);
        actionsGrid.removeAllColumns();
        actionsGrid.addColumn("id").setHeader("ACTIONS");
        actionsGrid.addColumn("variable");
        actionsGrid.addColumn("nonTerminals");
        actionsGrid.addColumn("expression");
        add(actionsGrid);
        add(createButton2, deleteButton2, editButton2);
    }

    private void setUpTemplates() {
        variable1.setWidth("500px");
        expression1.setWidth("500px");
        newTemplate1.setWidth("700px");
        newTemplate1.setHeight("700");
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
        newTemplate1.add(saveButton1);
        saveButton1.setId("save-button-1");

        variable2.setWidth("500px");
        nonterminals2.setWidth("500px");
        expression2.setWidth("500px");
        newTemplate2.setWidth("700px");
        newTemplate2.setHeight("700");
        newTemplate2.add(variable2);
        newTemplate2.add(nonterminals2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);
        saveButton2.setId("save-button-2");

        saveButton1.addClickListener(e -> {
            if(CFG.isVariable(variable1.getValue())){
                Rule rule = new Rule();

                List<String> expressions = Rule.expressionToArray(expression1.getValue());

                rule.setVariable(variable1.getValue());
                rule.setId(rules.size());
                rule.setExpressions(expressions);

                CFG.addRule(rule);
                try {
                    CFG.writeRules();
                    CFG.loadRules();
                    CNF.initialize();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                CFG.loadRules();
                CNF.initialize();
                rules = CFG.getRules();
                rulesGrid.setItems(rules);

                if (editPressed1) {
                    removeRule(selection1);
                    rulesGrid.deselectAll();
                    rulesGrid.setItems(rules);
                    try {
                        CFG.writeRules();
                        CFG.loadRules();
                        CNF.initialize();
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
            Action action = new Action();

            action.setId(actions.size());
            if(variable2.getValue().length()==0){
                action.setVariable("<ACTION>");
                Map<String, String> map = new HashMap<>();
                action.setNonTerminals(map);
                action.setExpression("I have no idea.");
            }else{
                action.setVariable(variable2.getValue());
                action.setNonTerminals(Action.stringToHashMap(nonterminals2.getValue()));
                action.setExpression(expression2.getValue());
            }

            if (Skills.isValidAction(action)) {
                Skills.addAction(action);
                try {
                    Skills.writeActions();
                    Skills.loadActions();
                    CNF.initialize();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                Skills.loadActions();
                CNF.initialize();
                actionsGrid.setItems(actions);

                if (editPressed2) {
                    removeAction(selection2);
                    actionsGrid.deselectAll();
                    actionsGrid.setItems(actions);
                    try {
                        Skills.writeActions();
                        Skills.loadActions();
                        CNF.initialize();
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }

                editPressed2 = false;
                selection2 = null;

                template2Empty();
                newTemplate2.close();

            } else {
                System.out.println("ERROR: no valid action");
                //TODO handle every type of error
            }
        });
    }

    private void template2Empty() {
        variable2.setValue("");
        nonterminals2.setValue("");
        expression2.setValue("");
        newTemplate2.removeAll();
        newTemplate2.add(variable2);
        newTemplate2.add(nonterminals2);
        newTemplate2.add(expression2);
        newTemplate2.add(saveButton2);
        variable2.setId("variable2");
    }

    private void template1Empty() {
        variable1.setValue("");
        expression1.setValue("");
        newTemplate1.removeAll();
        newTemplate1.add(variable1);
        newTemplate1.add(expression1);
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
}