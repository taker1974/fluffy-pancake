// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Collections;

/**
 * Сервис для работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
@Service
public class StudentService {

    public static final String STUDENT_NOT_EXISTS = "Объект Student с таким id не существует";
    public static final String STUDENT_EXISTS = "Объект Student с таким id уже существует";
    public static final String ID_CANNOT_BE_NULL = "Id не может быть null";
    public static final String STUDENT_CANNOT_BE_NULL = "Объект Student не может быть null";

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository students) {
        this.studentRepository = students;
    }

    /**
     * Добавление студента.
     *
     * @param student {@link Student}
     * @return {@link Student}
     * @throws NullPointerException     student равен null
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student addStudent(Student student) {
        if (student == null) {
            throw new NullPointerException(STUDENT_CANNOT_BE_NULL);
        }
        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new StudentNotFoundException(STUDENT_EXISTS);
        }
        return studentRepository.save(student);
    }

    /**
     * Получение студента.
     *
     * @param id id студента
     * @return {@link Student}
     * @throws NullPointerException     id равен null
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student getStudent(Long id) {
        if (id == null) {
            throw new NullPointerException(ID_CANNOT_BE_NULL);
        }
        var student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            throw new StudentNotFoundException(STUDENT_NOT_EXISTS);
        }
        return student;
    }

    /**
     * Обновление студента.
     *
     * @param student {@link Student}
     * @return {@link Student}
     * @throws NullPointerException     student равен null
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student updateStudent(Student student) {
        if (student == null) {
            throw new NullPointerException(STUDENT_CANNOT_BE_NULL);
        }
        var existingStudent = studentRepository.findById(student.getId()).orElse(null);
        if (existingStudent == null) {
            throw new StudentNotFoundException(STUDENT_NOT_EXISTS);
        }
        return studentRepository.save(student);
    }

    /**
     * Удаление студента.
     *
     * @param id id студента
     * @return {@link Student}
     * @throws NullPointerException     id равен null
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student deleteStudent(Long id) {
        if (id == null) {
            throw new NullPointerException(ID_CANNOT_BE_NULL);
        }
        var existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            throw new StudentNotFoundException(STUDENT_NOT_EXISTS);
        }
        studentRepository.deleteById(id);
        return existingStudent;
    }

    public Collection<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> findStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findStudentsByAgeBetween(int fromAge, int toAge) {
        return studentRepository.findByAgeBetween(fromAge, toAge);
    }
}
