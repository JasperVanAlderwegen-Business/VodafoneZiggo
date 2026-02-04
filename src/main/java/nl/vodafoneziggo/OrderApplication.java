package nl.vodafoneziggo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The OrderApplication class serves as the entry point for the Spring Boot
 * application. It is annotated with {@code @SpringBootApplication}, which
 * indicates it is a Spring Boot application and enables component scanning,
 * auto-configuration, and extra features.
 * <p>
 * The {@code main} method initializes and runs the application using
 * SpringApplication.
 */
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
