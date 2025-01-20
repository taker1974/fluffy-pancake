// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

/**
 * Сервис для работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.7
 */
@Service
public class StudentService {

    @NotNull
    private final StudentRepository studentRepository;

    @NotNull
    private final FacultyRepository facultyRepository;

    public StudentService(@NotNull StudentRepository studentRepository,
                          @NotNull FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
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
        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new StudentAlreadyExistsException();
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
        return studentRepository.findWithJoinFetch(id)
                .orElseThrow(StudentNotFoundException::new);
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
        if (studentRepository.findById(student.getId()).isEmpty()) {
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
        final Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
        studentRepository.deleteById(id);
        return student;
    }

    /**
     * Установка факультета простым способом.
     *
     * @param studentId id студента
     * @param facultyId id факультета
     * @return {@link Student}
     * @throws StudentNotFoundException студент с таким id не существует
     * @throws FacultyNotFoundException факультет с таким id не существует
     */
    public Student setFaculty(long studentId, long facultyId) {

        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        final Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(FacultyNotFoundException::new);

        student.setFaculty(faculty);
        studentRepository.save(student);
        return student;
    }

    /**
     * Сброс факультета.
     *
     * @param studentId id студента
     * @return {@link Student}
     * @throws StudentNotFoundException студент с таким id не существует
     */
    public Student resetFaculty(long studentId) {
        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.setFaculty(null);
        studentRepository.save(student);
        return student;
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
