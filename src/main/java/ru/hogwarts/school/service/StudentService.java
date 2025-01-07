// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * StudentService.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
@Service
public class StudentService {

    public static final String STUDENT_NOT_EXISTS = "Объект Student с таким id не существует";
    public static final String STUDENT_EXISTS = "Объект Student с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String STUDENT_CANNOT_BE_NULL = "Объект Student не может быть null";

    private final Map<Long, Student> students = HashMap.newHashMap(100);
    private long id = 0;

    public StudentService() {
        // ...
    }

    public Student addStudent(Student student) throws StudentServiceException {
        if (student == null) {
            throw new StudentServiceException(STUDENT_CANNOT_BE_NULL);
        }
        if (students.containsKey(student.getId())) {
            throw new StudentServiceException(STUDENT_EXISTS);
        }
        id++;
        try {
            return students.put(id, new Student(id, student.getName(), student.getAge()));
        } catch (StudentException e) {
            throw new StudentServiceException(e.getMessage());
        }
    }

    public Student getStudent(Long id) throws StudentServiceException {
        if (id == null) {
            throw new StudentServiceException(ID_CANNOT_BE_NULL);
        }
        if (students.containsKey(id)) {
            return students.get(id);
        }
        throw new StudentServiceException(STUDENT_NOT_EXISTS);
    }

    public Student updateStudent(Student student) throws StudentServiceException {
        if (student == null) {
            throw new StudentServiceException(STUDENT_CANNOT_BE_NULL);
        }
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
        }
        throw new StudentServiceException(STUDENT_NOT_EXISTS);
    }

    public Student deleteStudent(Long id) throws StudentServiceException {
        if (id == null) {
            throw new StudentServiceException(ID_CANNOT_BE_NULL);
        }
        if (!students.containsKey(id)) {
            throw new StudentServiceException(STUDENT_NOT_EXISTS);
        }
        return students.remove(id);
    }

    public Collection<Student> filterStudentsByAge(int age) {
        Collection<Student> result = new ArrayList<>(students.size());
        students.values().stream()
                .filter(student -> student.getAge() == age)
                .forEach(result::add);
        return result;
    }

    public Collection<Student> getStudentsAll() {
        return Collections.unmodifiableCollection(students.values());
    }
}
