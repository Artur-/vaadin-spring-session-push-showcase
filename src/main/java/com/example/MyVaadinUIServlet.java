package com.example;

import org.springframework.stereotype.Component;

import com.vaadin.spring.server.SpringVaadinServlet;

@Component("vaadinServlet")
public class MyVaadinUIServlet extends SpringVaadinServlet {

    private static final long serialVersionUID = 3407235182832472556L;

//    @Autowired
//    private AtmosphereFramework atmosphere;
//
//    @Override
//    protected void servletInitialized() throws ServletException {
//        super.servletInitialized();
//        //getService().addSessionInitListener(new MySessionInitListener());
//        log.debug("MyVaadinUIServlet initialized");
//
//        atmosphere.init(this.getServletConfig());
//    }

}
