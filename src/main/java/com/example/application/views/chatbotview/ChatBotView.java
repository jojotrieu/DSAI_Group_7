package com.example.application.views.chatbotview;

import com.example.application.services.ChatBot;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import java.util.ArrayList;
import java.util.List;

@Route(value = "chatbot", layout = MainView.class)
@PageTitle("ChatBot")
@CssImport("./styles/views/chatbot/chatbot.css")
@RouteAlias(value = "", layout = MainView.class)
public class ChatBotView extends HorizontalLayout {

    private TextField questionTextField;
    private Button clearButton = new Button("Clear Chat");
    private H4 thinking = new H4("ChatBot: Mmmm... Let me think.");
    private TextArea area = new TextArea();
    private ChatBot chatBot = new ChatBot();
    private String conversation = "";

    public ChatBotView() {
        setId("chatbot-view");
        area.setReadOnly(true);
        area.setId("text-area");
        questionTextField = new TextField("Ask me anything");
        questionTextField.setId("question-field");
        clearButton.setId("clear-button");
        questionTextField.addKeyPressListener(Key.ENTER, e -> {
            //disable Text Field while ChatBot is thinking
            questionTextField.setEnabled(false);
            //display question in H4
            String questionH4 = "You: " + questionTextField.getValue();
            conversation += questionH4 + "\n";
            //display "thinking" while ChatBot is thinking
            add(thinking);
            //get response from chatBot
            try {
                String responseString = chatBot.response(questionTextField.getValue());
                String responseH4 = "ChatBot: " + responseString;
                conversation += responseH4 + "\n";
                area.setValue(conversation);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            //clearButton Text Field
            questionTextField.clear();
            //re-enable Text Field
            questionTextField.setEnabled(true);
            remove(thinking);
            clearButton.setEnabled(true);
        });
        add(questionTextField);
        add(clearButton);
        add(area);
        clearButton.addClickListener(e -> {
            conversation = "";
            area.setValue(conversation);
            clearButton.setEnabled(false);
        });
        clearButton.setEnabled(false);
    }

}
