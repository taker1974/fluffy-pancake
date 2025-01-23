package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping(value = "/faculty")
@Tag(name = "Факультеты")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @Operation(summary = "Добавление нового факультета")
    @PostMapping
    public ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.addFaculty(faculty));
    }

    @Operation(summary = "Получение существующего факультета по id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @Operation(summary = "Обновление существующего факультета")
    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @Operation(summary = "Удаление существующего факультета")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) {
        return ResponseEntity.ok(facultyService.deleteFaculty(id));
    }

    @Operation(summary = "Поиск факультетов по точному совпадению \"цвета\"")
    @GetMapping(value = "/filter/color/{color}")
    public ResponseEntity<Collection<Faculty>> findFacultiesByColor(@PathVariable String color) {
        return ResponseEntity.ok(facultyService.findFacultiesByColor(color));
    }

    @Operation(summary = "Поиск факультетов по названию или по \"цвету\" без учёта регистра")
    @GetMapping(value = "/filter")
    public ResponseEntity<Collection<Faculty>> findFacultiesByNameOrColor(String name, String color) {
        return ResponseEntity.ok(facultyService.findFacultiesByNameOrColorIgnoreCase(name, color));
    }

    @Operation(summary = "Получение всех факультетов")
    @GetMapping
    public ResponseEntity<Collection<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @Operation(summary = "Получение всех студентов факультета")
    @GetMapping(value = "/{facultyId}/students")
    public ResponseEntity<Collection<Student>> findStudentsByFaculty(@PathVariable long facultyId) {
        return ResponseEntity.ok(facultyService.getFaculty(facultyId).getStudents());
    }
}
