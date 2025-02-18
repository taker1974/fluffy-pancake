package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.NameGenerator;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.tools.LogEx;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
        if (facultyRepository.findById(faculty.getId()).isEmpty()) {
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

    public static final int ADD_TEST_LIMIT = 100_000;
    public static final String TEST_NAME_SUFFIX = " [gen]";
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;

    public int addTestFaculties(int count,
                                int minNameLength, int maxNameLength) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "count = " + count);

        removeTestFaculties();

        if (count <= 0) {
            count = 1;
        } else if (count > ADD_TEST_LIMIT) {
            count = ADD_TEST_LIMIT;
        }

        if (minNameLength < MIN_NAME_LENGTH) {
            minNameLength = MIN_NAME_LENGTH;
        } else if (maxNameLength > MAX_NAME_LENGTH) {
            maxNameLength = MAX_NAME_LENGTH;
        }

        for (int i = 0; i < count; i++) {
            final Faculty faculty = new Faculty(0L,
                    NameGenerator.getName(minNameLength, maxNameLength, TEST_NAME_SUFFIX),
                    NameGenerator.getName(minNameLength, maxNameLength, null),
                    null);
            facultyRepository.save(faculty);
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return count;
    }

    public void removeTestFaculties() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);
        facultyRepository.deleteAll(facultyRepository.findByNameSuffix(TEST_NAME_SUFFIX));
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public String getLongestNameDumb() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        List<String> names = facultyRepository.findAll()
                .stream()
                .map(Faculty::getName).toList();

        String longestName = "";
        for (String name : names) {
            if (name.length() > longestName.length()) {
                longestName = name;
            }
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return longestName;
    }

    public String getLongestName() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        String name = facultyRepository.findAll()
                .parallelStream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return name;
    }

    public double getSumDumb(long limit) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "limit = " + limit);

        double sum = Stream.iterate(1L, a -> a + 1L).limit(1_000_000).reduce(0L, Long::sum);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING, "sum = " + sum);
        return sum;
    }

    public double getSum(long limit) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        double sum = LongStream.rangeClosed(1, limit).parallel().sum();

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return sum;
    }
}
