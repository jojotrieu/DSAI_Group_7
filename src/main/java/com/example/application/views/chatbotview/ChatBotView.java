package com.example.application.views.chatbotview;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "chatbot", layout = MainView.class)
@PageTitle("ChatBot")
@CssImport("./styles/views/helloworld/hello-world-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class ChatBotView extends HorizontalLayout {

    private TextField questions;
    private Button enter;

    public ChatBotView() {
        setId("hello-world-view");
        questions = new TextField("Ask me anything");
        questions.setSizeFull();
        questions.addKeyPressListener(Key.ENTER, e -> {
                Notification.show("Hello " + questions.getValue());
        });
        add(questions);
    }

}
