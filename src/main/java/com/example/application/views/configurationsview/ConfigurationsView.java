package com.example.application.views.configurationsview;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;

import java.util.ArrayList;
import java.util.List;

@Route(value = "configurations", layout = MainView.class)
@CssImport("./styles/views/configurations/configurations.css")
@PageTitle("Configurations")
public class ConfigurationsView extends Div {

    private ComboBox<String> loadMenu = new ComboBox<>();
    private Button loadButton = new Button("Load");
    private Button createButton = new Button("Create New");

    public ConfigurationsView() {
        setId("configurations-view");
        loadButton.setId("load-button");
        createButton.setId("create-button");
        List<String> templates = new ArrayList<>();
        templates.add("file1");
        templates.add("file2");
        templates.add("file3");
        templates.add("file4");
        templates.add("file5");
        templates.add("file6");
        loadMenu.setItems(templates);
        loadMenu.setLabel("Load Template");
        add(loadMenu,loadButton,createButton);

    }

}
