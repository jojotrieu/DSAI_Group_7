package com.example.application.views.settingsview;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "settings", layout = MainView.class)
//temporary css
@CssImport("./styles/views/configurations/configurations.css")
@PageTitle("Settings")
public class SettingsView extends Div {

    public SettingsView(){
        setId("settings-view");
        add(new Text("Any possible settings for the bot, maybe theme changer? Just cool stuff"));
    }

}
