// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

/**
 * Сервис для работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.5
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository students) {
        this.studentRepository = students;
    }

    /**
     * Добавление студента.
     *
     * @param student {@link Student}
     * @return {@link Student}
     * @throws NullStudentException          student равен null
     * @throws StudentAlreadyExistsException студент с таким id уже существует
     */
    public Student addStudent(Student student) {
        if (student == null) {
            throw new NullStudentException();
        }
        return studentRepository.save(student);
    }

    /**
     * Получение студента.
     *
     * @param id id студента
     * @return {@link Student}
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student getStudent(long id) {
        var student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            throw new StudentNotFoundException();
        }
        return student;
    }

    /**
     * Обновление студента.
     *
     * @param student {@link Student}
     * @return {@link Student}
     * @throws NullStudentException     student равен null
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student updateStudent(Student student) {
        if (student == null) {
            throw new NullStudentException();
        }
        var existingStudent = studentRepository.findById(student.getId());
        if (existingStudent.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return studentRepository.save(student);
    }

    /**
     * Удаление студента.
     *
     * @param id id студента
     * @return {@link Student}
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student deleteStudent(long id) {
        var existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            throw new StudentNotFoundException();
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
