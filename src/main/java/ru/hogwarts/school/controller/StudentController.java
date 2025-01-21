package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.interceptor.annotation.CheckStudent;
import ru.hogwarts.school.model.Student;

@RestController
@RequestMapping(value = "/student")
@Tag(name = "Студенты")
@RequiredArgsConstructor
public class StudentController {

    @Operation(summary = "Добавление нового студента")
    @PostMapping
    @CheckStudent
    public ResponseEntity<Student> addStudent(Student student) {
        return ResponseEntity.ok(student);
    }
}
