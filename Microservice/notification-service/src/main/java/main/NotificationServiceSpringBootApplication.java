package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@EnableCaching
public class NotificationServiceSpringBootApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(NotificationServiceSpringBootApplication.class, args);
        } catch (Exception e) {
            System.out.println("See logs");
        }
    }
}