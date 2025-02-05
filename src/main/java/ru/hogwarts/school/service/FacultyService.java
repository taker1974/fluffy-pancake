package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.tools.LogEx;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacultyService {

    Logger log = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public Faculty addFaculty(Faculty faculty) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, faculty);

        if (faculty == null) {
            throw new NullFacultyException();
        }
        if (facultyRepository.findById(faculty.getId()).isPresent()) {
            throw new FacultyAlreadyExistsException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(long id) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "id = " + id);
        return facultyRepository.findById(id)
                .orElseThrow(FacultyNotFoundException::new);
    }

    public Faculty updateFaculty(Faculty faculty) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, faculty);

        if (faculty == null) {
            throw new NullFacultyException();
        }
        if (facultyRepository.findById(faculty.getId()).isEmpty()){
            throw new FacultyNotFoundException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "id = " + id);

        final Optional<Faculty> optionalFaculty = facultyRepository.findById(id);
        if (optionalFaculty.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        facultyRepository.deleteById(id);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public List<Faculty> getAllFaculties() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN);
        return facultyRepository.findAll();
    }

    public List<Faculty> findFacultiesByColor(String color) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "color = " + color);
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findFacultiesByNameOrColorIgnoreCase(String name, String color) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN,
                "name = " + name + ", color = " + color);
        return facultyRepository.findByNameOrColorIgnoreCase(name, color);
    }
}
