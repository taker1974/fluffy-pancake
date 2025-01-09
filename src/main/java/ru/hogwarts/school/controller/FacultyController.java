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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.exception.FacultyServiceException;

import java.util.Collection;

/**
 * FacultyController.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
@RestController
@RequestMapping(value = "/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> addFaculty(Faculty faculty) throws FacultyServiceException {
        return ResponseEntity.ok(facultyService.addFaculty(faculty));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id) throws FacultyServiceException {
        return ResponseEntity.ok(facultyService.getFaculty(id));
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(Faculty faculty) throws FacultyServiceException {
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable long id) throws FacultyServiceException {
        return ResponseEntity.ok(facultyService.deleteFaculty(id));
    }

    @RequestMapping(value = "/filter/color/{color}")
    public ResponseEntity<Collection<Faculty>> filterFacultiesByColor(@PathVariable String color) {
        return ResponseEntity.ok(facultyService.filterFacultiesByColor(color));
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getFacultiesAll() {
        return ResponseEntity.ok(facultyService.getFacultiesAll());
    }
}

