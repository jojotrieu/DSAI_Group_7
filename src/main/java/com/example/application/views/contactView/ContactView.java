package com.example.application.views.contactView;

import com.example.application.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;


@Route(value = "contact", layout = MainView.class)
//temporary css
@CssImport("./styles/views/contact/contact.css")
@PageTitle("Contact")
public class ContactView extends Div {

    public ContactView() {
        setId("contact-view");

        Label group = new Label("GROUP 7");
        add(group);

        List<Person> team = new ArrayList<>();

        team.add(new Person("i9812912", "William", "De Clercq", "w.declercq@student.maastrichtunviersity.nl"));
        team.add(new Person("i6209306", "Adèle", "Imparato", "a.imparato@student.maastrichtunviersity.nl"));
        team.add(new Person("i6316220", "Jo", "Trieu", "n.trieu@student.maastrichtunviersity.nl"));
        team.add(new Person("i6810765", "Roy", "Withaar", "r.withaar@student.maastrichtunviersity.nl"));
        team.add(new Person("i9810321", "Thibault", "Van de Sompele", "t.vandesompele@student.maastrichtunviersity.nl"));
        team.add(new Person("i1291712", "Nico", "Grassetto", "n.grassetto@student.maastrichtunviersity.nl"));
        team.add(new Person("i0725374", "Alex", "Rosca", "alexei.rosca@student.maastrichtunviersity.nl"));

        Grid<Person> grid = new Grid<>(Person.class);
        grid.setItems(team);

        //grid.removeColumnByKey("studentID");

        grid.setColumns("firstName", "lastName", "email");
        add(grid);

    }
}


