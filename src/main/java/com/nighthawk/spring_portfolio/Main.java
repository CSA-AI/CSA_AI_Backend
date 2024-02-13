package com.nighthawk.spring_portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.awt.GraphicsEnvironment;

// @SpringBootApplication annotation is the key to building web applications with Java https://spring.io/projects/spring-boot
@SpringBootApplication
public class Main {

    // Starts a spring application as a stand-alone application from the main method
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        // SpringApplication.run(Main.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.headless(true);
        ConfigurableApplicationContext context = builder.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationStart() {
        System.out.println("Application started!");
        System.out.println("Admin Key: " + System.getenv("ADMIN_KEY"));
        System.out.println("Graphics Environment Headless? " + GraphicsEnvironment.isHeadless());
        Resource dbResource = new FileSystemResource("volumes/sqlite.db");

        // Check if the file exists
        if (dbResource.exists()) {
            System.out.println("sqlite.db file found");
        }
    }

}