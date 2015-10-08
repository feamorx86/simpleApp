package com.feamor.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by user on 04.08.2015.
 */

@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
public class Application {

    public static void main(String [] args) {
        SpringApplication.run(Application.class, args);
    }
}
