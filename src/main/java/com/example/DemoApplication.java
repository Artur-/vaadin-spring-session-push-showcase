package com.example;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.vaadin.spring.session.redis.VaadinSessionRewriteFilter;

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

}
