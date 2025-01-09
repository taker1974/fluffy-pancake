// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyServiceException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Collections;

/**
 * Сервис для работы с факультетами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@Service
public class FacultyService {

    public static final String FACULTY_NOT_EXISTS = "Объект Faculty с таким id не существует";
    public static final String FACULTY_EXISTS = "Объект Faculty с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String FACULTY_CANNOT_BE_NULL = "Объект Faculty не может быть null";

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository faculties) {
        this.facultyRepository = faculties;
    }

    public Faculty addFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        if (facultyRepository.findById(faculty.getId()).isPresent()) {
            throw new FacultyServiceException(FACULTY_EXISTS);
        }
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        var faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        return faculty;
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        var existingFaculty = facultyRepository.findById(faculty.getId()).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        var existingFaculty = facultyRepository.findById(id).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        facultyRepository.deleteById(id);
        return existingFaculty;
    }

    public Collection<Faculty> filterFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultiesAll() {
        return Collections.unmodifiableCollection(facultyRepository.findAll());
    }
}
