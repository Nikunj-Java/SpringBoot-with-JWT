package com.neueda.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SpringBoot2Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringBoot2Application.class);
        application.addInitializers(PostgresDatabaseInitializer::initialize);
        application.run(args);
    }

}
