// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FacultyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * FacultyService.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
@Service
public class FacultyService {

    public static final String FACULTY_NOT_EXISTS = "Объект Faculty с таким id не существует";
    public static final String FACULTY_EXISTS = "Объект Faculty с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String FACULTY_CANNOT_BE_NULL = "Объект Faculty не может быть null";

    private final Map<Long, Faculty> faculties = HashMap.newHashMap(100);
    private long id = 0;

    public FacultyService() {
        // ...
    }

    public Faculty addFaculty(Faculty faculty) throws FacultyServiceException {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        if (faculties.containsKey(faculty.getId())) {
            throw new FacultyServiceException(FACULTY_EXISTS);
        }
        id++;
        try {
            return faculties.put(id, new Faculty(id, faculty.getName(), faculty.getColor()));
        } catch (FacultyException e) {
            throw new FacultyServiceException(e.getMessage());
        }
    }

    public Faculty getFaculty(Long id) throws FacultyServiceException {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        if (faculties.containsKey(id)) {
            return faculties.get(id);
        }
        throw new FacultyServiceException(FACULTY_NOT_EXISTS);
    }

    public Faculty updateFaculty(Faculty faculty) throws FacultyServiceException {
        if (faculty == null) {
            throw new FacultyServiceException(FACULTY_CANNOT_BE_NULL);
        }
        if (faculties.containsKey(faculty.getId())) {
            faculties.put(faculty.getId(), faculty);
        }
        throw new FacultyServiceException(FACULTY_NOT_EXISTS);
    }

    public Faculty deleteFaculty(Long id) throws FacultyServiceException {
        if (id == null) {
            throw new FacultyServiceException(ID_CANNOT_BE_NULL);
        }
        if (!faculties.containsKey(id)) {
            throw new FacultyServiceException(FACULTY_NOT_EXISTS);
        }
        return faculties.remove(id);
    }

    public Collection<Faculty> filterFacultiesByColor(String color) {
        Collection<Faculty> result = new ArrayList<>(faculties.size());
        if (color == null) {
            return result;
        }
        if (color.isEmpty()) {
            return result;
        }

        faculties.values().stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .forEach(result::add);
        return result;
    }

    public Collection<Faculty> getFacultiesAll() {
        return Collections.unmodifiableCollection(faculties.values());
    }
}
