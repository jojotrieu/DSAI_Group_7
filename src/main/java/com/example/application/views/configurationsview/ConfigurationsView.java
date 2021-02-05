package com.example.application.views.configurationsview;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;

@Route(value = "configurations", layout = MainView.class)
@PageTitle("Configurations")
public class ConfigurationsView extends Div {

    public ConfigurationsView() {
        setId("about-view");
        add(new Text("Here will be the configurations."));
    }

}
