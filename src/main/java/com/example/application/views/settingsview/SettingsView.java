package com.example.application.views.settingsview;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "settings", layout = MainView.class)
@CssImport("./styles/views/settings/settings.css")
@PageTitle("Settings")
public class SettingsView extends VerticalLayout {
    public static String selectedAlgorithm = "Haar Cascade";
    public static String selectedType = "Background";

    public SettingsView() {
        setId("settings-view");
        Button themeButton = new Button("Enable dark theme");
        themeButton.setId("theme-button");
        Select<String> algorithms = new Select<>();
        algorithms.setId("algorithm-select");
        Select<String> typeOfCheck = new Select<>();
        typeOfCheck.setId("type-select");
        algorithms.setItems("Haar Cascade", "Skin Color", "PCA");
        algorithms.setLabel("Face detection");
        algorithms.setPlaceholder("Haar Cascade");
        typeOfCheck.setItems("Background", "Login check");
        typeOfCheck.setLabel("Type of check");
        typeOfCheck.setPlaceholder("Background");
        themeButton.addClickListener(e -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();

            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                themeButton.setText("Enable dark theme");
            } else {
                themeList.add(Lumo.DARK);
                themeButton.setText("Enable light theme");
            }
        });
        algorithms.addValueChangeListener(event ->
                selectedAlgorithm = event.getValue());
        typeOfCheck.addValueChangeListener(event ->
                selectedType = event.getValue());

        add(algorithms);
        add(typeOfCheck);
        add(themeButton);
    }

}
