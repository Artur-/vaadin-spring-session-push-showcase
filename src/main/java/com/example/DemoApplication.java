package com.example;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AnnotationProcessor;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicy.ATMOSPHERE_RESOURCE_POLICY;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.spring.bean.AtmosphereSpringContext;
import org.atmosphere.util.VoidAnnotationProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.vaadin.spring.session.redis.VaadinSessionRewriteFilter;

import com.vaadin.server.communication.PushAtmosphereHandler;
import com.vaadin.shared.communication.PushConstants;

@SpringBootApplication
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public Filter vaadinSessionRewriteFilter() {
        return new VaadinSessionRewriteFilter();
    }

//    @Bean
//    public SessionSupport atmosphereSessionSupport() {
//        return new SessionSupport();
//    }

    @Bean
    public AtmosphereFramework atmosphereFramework() throws ServletException, InstantiationException, IllegalAccessException {
        AtmosphereFramework atmosphere = new AtmosphereFramework(false, false) {
            @Override
            protected void analytics() {
                // Overridden to disable version number check
            }
        };
        atmosphere.addAtmosphereHandler("/*", new PushAtmosphereHandler());
        atmosphere.addInitParameter(ApplicationConfig.BROADCASTER_CACHE,
                UUIDBroadcasterCache.class.getName());
        atmosphere.addInitParameter(ApplicationConfig.ANNOTATION_PROCESSOR,
                VoidAnnotationProcessor.class.getName());
        atmosphere.addInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT,
                "true");
        atmosphere.addInitParameter(ApplicationConfig.MESSAGE_DELIMITER,
                String.valueOf(PushConstants.MESSAGE_DELIMITER));
        atmosphere.addInitParameter(
                ApplicationConfig.DROP_ACCESS_CONTROL_ALLOW_ORIGIN_HEADER,
                "false");
        // Disable heartbeat (it does not emit correct events client side)
        // https://github.com/Atmosphere/atmosphere-javascript/issues/141
        atmosphere.addInitParameter(
                ApplicationConfig.DISABLE_ATMOSPHEREINTERCEPTORS,
                HeartbeatInterceptor.class.getName());

        final String bufferSize = String
                .valueOf(PushConstants.WEBSOCKET_BUFFER_SIZE);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_BUFFER_SIZE,
                bufferSize);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_MAXTEXTSIZE,
                bufferSize);
        atmosphere.addInitParameter(ApplicationConfig.WEBSOCKET_MAXBINARYSIZE,
                bufferSize);
        atmosphere.addInitParameter(
                ApplicationConfig.PROPERTY_ALLOW_SESSION_TIMEOUT_REMOVAL,
                "false");
        // This prevents Atmosphere from recreating a broadcaster after it has
        // already been destroyed when the servlet is being undeployed
        // (see #20026)
        atmosphere.addInitParameter(ApplicationConfig.RECOVER_DEAD_BROADCASTER,
                "false");
        // Disable Atmosphere's message about commercial support
        atmosphere.addInitParameter("org.atmosphere.cpr.showSupportMessage",
                "false");

        // Ensure the client-side knows how to split the message stream
        // into individual messages when using certain transports
        AtmosphereInterceptor trackMessageSize = new TrackMessageSizeInterceptor();
        trackMessageSize.configure(atmosphere.getAtmosphereConfig());
        atmosphere.interceptor(trackMessageSize);
        return atmosphere;
    }

    @Bean
    public BroadcasterFactory broadcasterFactory() throws ServletException, InstantiationException, IllegalAccessException {
        return atmosphereFramework().getAtmosphereConfig().getBroadcasterFactory();
    }

    @Bean
    public AtmosphereSpringContext atmosphereSpringContext() {
        AtmosphereSpringContext atmosphereSpringContext = new AtmosphereSpringContext();
        Map<String, String> map = new HashMap<>();
        map.put("org.atmosphere.cpr.broadcasterClass", org.atmosphere.cpr.DefaultBroadcaster.class.getName());
        map.put(AtmosphereInterceptor.class.getName(), TrackMessageSizeInterceptor.class.getName());
        map.put(AnnotationProcessor.class.getName(), VoidAnnotationProcessor.class.getName());
        map.put("org.atmosphere.cpr.broadcasterLifeCyclePolicy", ATMOSPHERE_RESOURCE_POLICY.IDLE_DESTROY.toString());
        atmosphereSpringContext.setConfig(map);
        return atmosphereSpringContext;
    }

}
