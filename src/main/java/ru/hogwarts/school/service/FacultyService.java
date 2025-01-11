// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

/**
 * Сервис для работы с факультетами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.5
 */
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository faculties) {
        this.facultyRepository = faculties;
    }

    /**
     * Добавление факультета.
     *
     * @param faculty {@link Faculty}
     * @return {@link Faculty}
     * @throws NullFacultyException          faculty равен null
     * @throws FacultyAlreadyExistsException факультет с таким id уже существует
     */
    public Faculty addFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new NullFacultyException();
        }
        if (facultyRepository.findById(faculty.getId()).isPresent()) {
            throw new FacultyAlreadyExistsException();
        }
        return facultyRepository.save(faculty);
    }

    /**
     * Получение факультета.
     *
     * @param id id факультета
     * @return {@link Faculty}
     * @throws FacultyNotFoundException факультет с таким id не существует
     */
    public Faculty getFaculty(long id) {
        var faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            throw new FacultyNotFoundException();
        }
        return faculty;
    }

    /**
     * Обновление факультета.
     *
     * @param faculty {@link Faculty}
     * @return {@link Faculty}
     * @throws NullFacultyException     faculty равен null
     * @throws FacultyNotFoundException факультет с таким id не существует
     */
    public Faculty updateFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new NullFacultyException();
        }
        var existingFaculty = facultyRepository.findById(faculty.getId()).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyNotFoundException();
        }
        return facultyRepository.save(faculty);
    }

    /**
     * Удаление факультета.
     *
     * @param id id факультета
     * @return {@link Faculty}
     * @throws FacultyNotFoundException факультет с таким id не существует
     */
    public Faculty deleteFaculty(long id) {
        var existingFaculty = facultyRepository.findById(id).orElse(null);
        if (existingFaculty == null) {
            throw new FacultyNotFoundException();
        }
        facultyRepository.deleteById(id);
        return existingFaculty;
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> findFacultiesByNameOrColorIgnoreCase(String name, String color) {
        return facultyRepository.findByNameOrColorIgnoreCase(name, color);
    }
}
