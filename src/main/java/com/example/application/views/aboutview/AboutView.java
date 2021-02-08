package com.example.application.views.aboutview;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainView.class)
@CssImport("./styles/views/about/about.css")
@PageTitle("About")

public class AboutView extends Div {

    public AboutView(){
        setId("about-view");
        add(new Text("Why we did it, how we did it and maybe some general info about the used algorithms"));
    }

}
