package com.example;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI
@Theme(ValoTheme.THEME_NAME)
@Push(transport = Transport.LONG_POLLING)
public class VaadinUI extends UI {

    private static final long serialVersionUID = 8135679809390061654L;

    private HorizontalLayout layout = new HorizontalLayout();

    @Override
    protected void init(VaadinRequest request) {
        layout.addComponent(new Button("Test", e -> e.getButton().setCaption("Ok")));
        setContent(layout);

        // Start the data feed thread
        new FeederThread().start();

    }


    class FeederThread extends Thread {
        @Override
        public void run() {
            try {
                access(() -> {
                    layout.addComponent(new Label("Hallo"));
                });
                Thread.sleep(10000);
                access(() -> {
                    layout.addComponent(new Label("Du"));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
