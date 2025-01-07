// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultiesRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * FacultyService.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@Service
public class FacultyService {

    public static final int GET_INITIAL_CAPACITY = 100;
    public static final String FACULTY_NOT_EXISTS = "Объект Faculty с таким id не существует";
    public static final String FACULTY_EXISTS = "Объект Faculty с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String FACULTY_CANNOT_BE_NULL = "Объект Faculty не может быть null";

    private final FacultiesRepository faculties;

    public FacultyService(FacultiesRepository faculties) {
        this.faculties = faculties;
    }

    public Faculty addFaculty(Faculty faculty) throws FacultyServiceException {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        if (faculties.findById(faculty.getId()).isPresent()) {
            throw new FacultyServiceException(FACULTY_EXISTS);
        }
        return faculties.save(faculty);
    }

    public Faculty getFaculty(Long id) throws FacultyServiceException {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        var faculty = faculties.findById(id).orElse(null);
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        return faculty;
    }

    public Faculty updateFaculty(Faculty faculty) throws FacultyServiceException {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        var existingFaculty = faculties.findById(faculty.getId()).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        return faculties.save(faculty);
    }

    public Faculty deleteFaculty(Long id) throws FacultyServiceException {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        var existingFaculty = faculties.findById(id).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        faculties.deleteById(id);
        return existingFaculty;
    }

    public Collection<Faculty> filterFacultiesByColor(String color) {
        Collection<Faculty> result = new ArrayList<>(GET_INITIAL_CAPACITY);
        if (color == null) {
            return result;
        }
        if (color.isEmpty()) {
            return result;
        }

        faculties.findAll().stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .forEach(result::add);
        return result;
    }

    public Collection<Faculty> getFacultiesAll() {
        return Collections.unmodifiableCollection(faculties.findAll());
    }
}
