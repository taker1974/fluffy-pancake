// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.StudentServiceException;

import java.util.Collection;

/**
 * StudentController.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
@RestController
@RequestMapping(value = "/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(Student student) throws StudentServiceException {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) throws StudentServiceException {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(Student student) throws StudentServiceException {
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) throws StudentServiceException {
        return ResponseEntity.ok(studentService.deleteStudent(id));
    }

    @RequestMapping(value = "/filter/age/{age}")
    public ResponseEntity<Collection<Student>> filterStudentsByAge(int age) {
        return ResponseEntity.ok(studentService.filterStudentsByAge(age));
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getStudentsAll() {
        return ResponseEntity.ok(studentService.getStudentsAll());
    }
}

