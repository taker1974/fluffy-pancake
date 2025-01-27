package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping(value = "/student")
@Tag(name = "Студенты")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового студента. Возвращает id нового студента")
    @PostMapping
    public long addStudent(@RequestBody Student student) {
        return studentService.addStudent(student).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение студента по id")
    @GetMapping(value = "/{id}")
    public Student getStudent(@PathVariable long id) {
        return studentService.getStudent(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление существующего студента")
    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление существующего студента по id")
    @DeleteMapping(value = "/{id}")
    public void deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Установка факультета студента по id студента и id факультета")
    @PatchMapping(value = "/{studentId}/faculty/{facultyId}")
    public void setFaculty(@PathVariable long studentId, @PathVariable long facultyId) {
        studentService.setFaculty(studentId, facultyService.getFaculty(facultyId));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение факультета студента по id студента")
    @GetMapping(value = "/{id}/faculty")
    public Faculty getFaculty(@PathVariable long id) {
        return studentService.getStudent(id).getFaculty();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сброс факультета для студента studentId")
    @PatchMapping(value = "/{studentId}/faculty/reset")
    public void resetFaculty(@PathVariable long studentId) {
        studentService.resetFaculty(studentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех студентов")
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск студентов по точному совпадению возраста")
    @GetMapping(value = "/filter/age/{age}")
    public List<Student> findStudentsByAge(@PathVariable int age) {
        return studentService.findStudentsByAge(age);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск студентов по диапазону возраста")
    @GetMapping(value = "/filter/age/between")
    public List<Student> findStudentsByAgeBetween(int fromAge, int toAge) {
        return studentService.findStudentsByAgeBetween(fromAge, toAge);
    }
}
