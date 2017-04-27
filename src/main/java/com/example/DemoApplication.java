package com.example;

import org.atmosphere.cpr.AtmosphereResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class DemoApplication {

    public static AtmosphereResource storedResource;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
