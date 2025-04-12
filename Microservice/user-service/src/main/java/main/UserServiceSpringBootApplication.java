package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class UserServiceSpringBootApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(UserServiceSpringBootApplication.class, args);
        } catch (Exception e) {
            System.out.println("See logs");
        }
    }
}