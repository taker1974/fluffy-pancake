package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/faculty")
@Tag(name = "Факультеты")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового факультета. Возвращает id нового факультета")
    @PostMapping("/add")
    public Long addFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение существующего факультета по id")
    @GetMapping(value = "/{id}")
    public Faculty getFaculty(@PathVariable long id) {
        return facultyService.getFaculty(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление существующего факультета")
    @PutMapping("/update")
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление существующего факультета")
    @DeleteMapping(value = "/delete/{id}")
    public void deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск факультетов по точному совпадению \"цвета\"")
    @GetMapping(value = "/filter/color")
    public List<Faculty> findFacultiesByColor(String color) {
        return facultyService.findFacultiesByColor(color);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск факультетов по названию или по \"цвету\" без учёта регистра")
    @GetMapping(value = "/filter")
    public List<Faculty> findFacultiesByNameOrColor(String name, String color) {
        return facultyService.findFacultiesByNameOrColorIgnoreCase(name, color);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех факультетов")
    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение студентов факультета")
    @GetMapping(value = "/{facultyId}/students")
    public Set<Student> findStudentsByFaculty(@PathVariable long facultyId) {
        return facultyService.getFaculty(facultyId).getStudents();
    }
}
