package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping(value = "/student")
@Tag(name = "Студенты")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    @Operation(summary = "Добавление нового студента")
    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @Operation(summary = "Получение студента по id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @Operation(summary = "Обновление существующего студента")
    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @Operation(summary = "Удаление существующего студента по id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        return ResponseEntity.ok(studentService.deleteStudent(id));
    }

    @Operation(summary = "Установка факультета студента по id студента и id факультета")
    @PatchMapping(value = "/{studentId}/faculty/{facultyId}")
    public ResponseEntity<Student> setFaculty(@PathVariable long studentId, @PathVariable long facultyId) {
        return ResponseEntity.ok(studentService.setFaculty(studentId, facultyService.getFaculty(facultyId)));
    }

    @Operation(summary = "Получение факультета студента по id студента")
    @GetMapping(value = "/{id}/faculty")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        return ResponseEntity.ok(studentService.getStudent(id).getFaculty());
    }

    @Operation(summary = "Сброс факультета для студента studentId")
    @PatchMapping(value = "/{studentId}/faculty/reset")
    public ResponseEntity<Student> resetFaculty(@PathVariable long studentId) {
        return ResponseEntity.ok(studentService.resetFaculty(studentId));
    }

    @Operation(summary = "Получение всех студентов")
    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Operation(summary = "Поиск студентов по точному совпадению возраста")
    @GetMapping(value = "/filter/age/{age}")
    public ResponseEntity<Collection<Student>> findStudentsByAge(@PathVariable int age) {
        return ResponseEntity.ok(studentService.findStudentsByAge(age));
    }

    @Operation(summary = "Поиск студентов по диапазону возраста")
    @GetMapping(value = "/filter/age/between")
    public ResponseEntity<Collection<Student>> findStudentsByAgeBetween(int fromAge, int toAge) {
        return ResponseEntity.ok(studentService.findStudentsByAgeBetween(fromAge, toAge));
    }
}
