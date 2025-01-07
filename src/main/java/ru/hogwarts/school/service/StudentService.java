// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentsRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * StudentService.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@Service
public class StudentService {

    public static final int GET_INITIAL_CAPACITY = 100;
    public static final String STUDENT_NOT_EXISTS = "Объект Student с таким id не существует";
    public static final String STUDENT_EXISTS = "Объект Student с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String STUDENT_CANNOT_BE_NULL = "Объект Student не может быть null";

    private final StudentsRepository students;

    public StudentService(StudentsRepository students) {
        this.students = students;
    }

    public Student addStudent(Student student) throws StudentServiceException {
        if (student == null) {
            throw new StudentServiceException(STUDENT_CANNOT_BE_NULL);
        }
        if (students.findById(student.getId()).isPresent()) {
            throw new StudentServiceException(STUDENT_EXISTS);
        }
        return students.save(student);
    }

    public Student getStudent(Long id) throws StudentServiceException {
        if (id == null) {
            throw new StudentServiceException(ID_CANNOT_BE_NULL);
        }
        var student = students.findById(id).orElse(null);
        if (student == null) {
            throw new StudentServiceException(STUDENT_NOT_EXISTS);
        }
        return student;
    }

    public Student updateStudent(Student student) throws StudentServiceException {
        if (student == null) {
            throw new StudentServiceException(STUDENT_CANNOT_BE_NULL);
        }
        var existingStudent = students.findById(student.getId()).orElse(null);
        if (existingStudent == null) {
            throw new StudentServiceException(STUDENT_NOT_EXISTS);
        }
        return students.save(student);
    }

    public Student deleteStudent(Long id) throws StudentServiceException {
        if (id == null) {
            throw new StudentServiceException(ID_CANNOT_BE_NULL);
        }
        var existingStudent = students.findById(id).orElse(null);
        if (existingStudent == null) {
            throw new StudentServiceException(STUDENT_NOT_EXISTS);
        }
        students.deleteById(id);
        return existingStudent;
    }

    public Collection<Student> filterStudentsByAge(int age) {
        Collection<Student> result = new ArrayList<>(GET_INITIAL_CAPACITY);
        students.findAll().stream()
                .filter(student -> student.getAge() == age)
                .forEach(result::add);
        return result;
    }

    public Collection<Student> getStudentsAll() {
        return Collections.unmodifiableCollection(students.findAll());
    }
}
