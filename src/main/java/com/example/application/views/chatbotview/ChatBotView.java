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
    private Button clear = new Button("Clear Chat");
    private H4 thinking = new H4("ChatBot: Mmmm... Let me think.");
    private ChatBot chatBot = new ChatBot();

    public ChatBotView() {
        setId("chatbot-view");
        List<H4> exchange = new ArrayList<>();
        questionTextField = new TextField("Ask me anything");
        questionTextField.setSizeFull();
        questionTextField.addKeyPressListener(Key.ENTER, e -> {
                //disable Text Field while ChatBot is thinking
                questionTextField.setEnabled(false);
                //display question in H4
                H4 questionH4 = new H4("You: " + questionTextField.getValue());
                add(questionH4);
                exchange.add(questionH4);
                //display "thinking" while ChatBot is thinking
                add(thinking);
                //get response from chatBot
                try {
                    String responseString = chatBot.response(questionTextField.getValue());
                    H4 responseH4 = new H4("ChatBot: " + responseString);
                    add(responseH4);
                    exchange.add(responseH4);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //clear Text Field
                questionTextField.clear();
                //re-enable Text Field
                questionTextField.setEnabled(true);
                remove(thinking);
                clear.setEnabled(true);
        });
        add(questionTextField);
        add(clear);
        clear.addClickListener( e -> {
            for(H4 h4: exchange){
                remove(h4);
            }
            clear.setEnabled(false);
        });
        clear.setEnabled(false);
    }

}
