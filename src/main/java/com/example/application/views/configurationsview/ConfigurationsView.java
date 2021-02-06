package com.example.application.views.configurationsview;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

import java.util.ArrayList;
import java.util.List;

@Route(value = "configurations", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations.css")
@PageTitle("Configurations")
public class ConfigurationsView extends Div {

    private Dialog newTemplate = new Dialog();
    private Button addTemplate = new Button("New");
    private Button deleteEntry = new Button("Delete");
    private Button editEntry = new Button("Edit");
    private Button submitTemplate = new Button("Submit");

    private TextField request = new TextField("Request");
    private TextField response = new TextField("Response");

    TreeGrid<String> grid = new TreeGrid<>();

    public ConfigurationsView() {
        setId("configurations-view");
        request.setWidth("500px");
        response.setWidth("500px");
        newTemplate.setWidth("550px");
        newTemplate.setHeight("300");
        newTemplate.add(request);
        newTemplate.add(response);
        newTemplate.add(submitTemplate);
        addTemplate.setId("");
        request.setId("request-textfield");
        response.setId("response-textfield");
        deleteEntry.setId("delete-button");
        addTemplate.setId("add-button");
        editEntry.setId("edit-button");
        add(new H4("Templates Editor"));
        add(grid);
        add(addTemplate, deleteEntry,editEntry);
        addTemplate.addClickListener(e -> {
            newTemplate.open();
        });
        submitTemplate.addClickListener(e-> {
           newTemplate.close();
        });
        deleteEntry.setEnabled(false);
        editEntry.setEnabled(false);
    }

}
