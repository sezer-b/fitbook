package dev.babat.spring.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendApplication {

    private BackendApplication() {
    }

    static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
