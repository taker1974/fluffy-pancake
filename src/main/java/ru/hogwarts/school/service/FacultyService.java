package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public Faculty addFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new NullFacultyException();
        }
        if (facultyRepository.findById(faculty.getId()).isPresent()) {
            throw new FacultyAlreadyExistsException();
        }
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(FacultyNotFoundException::new);
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (faculty == null) {
            throw new NullFacultyException();
        }
        if (facultyRepository.findById(faculty.getId()).isEmpty()){
            throw new FacultyNotFoundException();
        }
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(long id) {
        final Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(FacultyNotFoundException::new);
        facultyRepository.deleteById(id);
        return existingFaculty;
    }

    public Collection<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public Collection<Faculty> findFacultiesByNameOrColorIgnoreCase(String name, String color) {
        return facultyRepository.findByNameOrColorIgnoreCase(name, color);
    }

    public Collection<Student> getStudents(long facultyId) {
        return facultyRepository.findStudents(facultyId);
    }
}
