package org.vaadin.spring.session.redis;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Spring Session with Redis doesn't work out-of-the-box with Vaadin. The issue is that the objects in the session are sent to Redis
 * before the VaadinSession instance has been fully configured which causes an old version of the instance to be persisted in Redis.
 * The problem resides in the RedisOperationsSessionRepository class which unfortunatelly doesn't have enough extension options to
 * fix the problem with inheritance for example.
 * <p>
 * This Servlet Filter kicks in at the end of a request to "rewrite" the VaadinSession instance into the HttpSession in order to
 * persist the updated VaadinSession instance in Redis.
 *
 * @author Alejandro Duarte.
 */
@WebFilter
public class VaadinSessionRewriteFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            if (!httpServletRequest.getRequestURI().contains("/VAADIN") && !httpServletRequest.getRequestURI().startsWith("/vaadinServlet")) {
                HttpSession session = httpServletRequest.getSession(false);

                if (session != null) {
                    String vaadinSessionKey = "com.vaadin.server.VaadinSession.myVaadinUIServlet";
                    Object attributeSession = session.getAttribute(vaadinSessionKey);
                    session.setAttribute(vaadinSessionKey, attributeSession);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    }

}
