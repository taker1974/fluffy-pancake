// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.util.Collection;

/**
 * Контроллер для работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@RestController
@RequestMapping(value = "/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(Student student) {
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        return ResponseEntity.ok(studentService.deleteStudent(id));
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @RequestMapping(value = "/filter/age/{age}")
    public ResponseEntity<Collection<Student>> findStudentsByAge(int age) {
        return ResponseEntity.ok(studentService.findStudentsByAge(age));
    }

    @RequestMapping(value = "/filter/age")
    public ResponseEntity<Collection<Student>> findStudentsByAgeBetween(int fromAge, int toAge) {
        return ResponseEntity.ok(studentService.findStudentsByAgeBetween(fromAge, toAge));
    }

    @GetMapping(value = "/faculty/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        return ResponseEntity.ok(studentService.getStudent(id).getFaculty());
    }
}
