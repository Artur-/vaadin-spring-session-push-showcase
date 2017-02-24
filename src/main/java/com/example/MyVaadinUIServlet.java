package com.example;

import javax.servlet.ServletException;

import org.atmosphere.cpr.AtmosphereFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.spring.server.SpringVaadinServlet;

import lombok.extern.slf4j.Slf4j;

@Component("vaadinServlet")
@Slf4j
public class MyVaadinUIServlet extends SpringVaadinServlet {

    private static final long serialVersionUID = 3407235182832472556L;

    @Autowired
    private AtmosphereFramework atmosphere;

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        //getService().addSessionInitListener(new MySessionInitListener());
        log.debug("MyVaadinUIServlet initialized");

        atmosphere.init(this.getServletConfig());
    }

}
