package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.interceptor.annotation.CheckStudent;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student addStudent(Student student) {
        if (student == null) {
            throw new NullStudentException();
        }
        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new StudentAlreadyExistsException();
        }
        return studentRepository.save(student);
    }

    public Student getStudent(long id) {
        return studentRepository.findWithJoinFetch(id)
                .orElseThrow(StudentNotFoundException::new);
    }

    @CheckStudent
    public Student updateStudent(Student student) {
        if (student == null) {
            throw new NullStudentException();
        }
        if (studentRepository.findById(student.getId()).isEmpty()) {
            throw new StudentNotFoundException();
        }
        return studentRepository.save(student);
    }

    public Student deleteStudent(long id) {
        final Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
        studentRepository.deleteById(id);
        return student;
    }

    /**
     * Сброс факультета.
     *
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
