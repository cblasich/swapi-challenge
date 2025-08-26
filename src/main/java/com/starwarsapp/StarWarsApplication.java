package com.starwarsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@SpringBootApplication
public class StarWarsApplication
{
    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(StarWarsApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port",
                System.getenv("PORT") != null ? System.getenv("PORT") : "8080"));
        app.run(args);

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
