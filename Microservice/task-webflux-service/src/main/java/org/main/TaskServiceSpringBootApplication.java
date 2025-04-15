package org.main;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
//@EnableCaching
//@EnableScheduling
//@EnableAsync
public class TaskServiceSpringBootApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(TaskServiceSpringBootApplication.class, args);
        } catch (Exception e) {
            System.out.println("See logs");
        }
    }
}