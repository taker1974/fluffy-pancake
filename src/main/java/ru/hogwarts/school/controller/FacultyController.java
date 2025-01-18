// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

/**
 * Контроллер для работы с факультетами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.7
 */
@RestController
@RequestMapping(value = "/faculty")
@Tag(name = "Факультеты")
public class FacultyController {

    // Согласно правилу java:S4488 здесь НЕ используется @RequestMapping.

    @NotNull
    private final FacultyService facultyService;

    public FacultyController(@NotNull FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @Operation(summary = "Добавление нового факультета")
    @PostMapping
    public ResponseEntity<Faculty> addFaculty(Faculty faculty)  {
        return ResponseEntity.ok(facultyService.addFaculty(faculty));
    }

    @Operation(summary = "Получение существующего факультета по id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id)  {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @Operation(summary = "Обновление существующего факультета")
    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(Faculty faculty)  {
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @Operation(summary = "Удаление существующего факультета")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id)  {
        return ResponseEntity.ok(facultyService.deleteFaculty(id));
    }

    @Operation(summary = "Поиск факультетов по точному совпадению \"цвета\"")
    @GetMapping(value = "/filter/color/{color}")
    public ResponseEntity<Collection<Faculty>> findFacultiesByColor(@PathVariable String color) {
        return ResponseEntity.ok(facultyService.findFacultiesByColor(color));
    }

    @Operation(summary = "Получение всех факультетов")
    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @Operation(summary = "Получение всех студентов факультета")
    @GetMapping(value = "/{id}/students")
    public ResponseEntity<Collection<Student>> findStudentsByFaculty(@PathVariable long id) {
        return ResponseEntity.ok(facultyService.getFaculty(id).getStudents());
    }
}
