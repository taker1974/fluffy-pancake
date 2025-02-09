package ru.hogwarts.school;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hogwarts.school.model.NameGenerator;

@SpringBootApplication
public class SchoolApplication {

    public static void main(String[] args) {

        SpringApplication.run(SchoolApplication.class, args);
    }
}
