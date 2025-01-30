package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

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
        return studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
    }

    public Student updateStudent(Student student) {
        if (student == null) {
            throw new NullStudentException();
        }
        if (studentRepository.findById(student.getId()).isEmpty()) {
            throw new StudentNotFoundException();
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        final Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(id);
    }

    public void setFaculty(long studentId, Faculty faculty) {
        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.setFaculty(faculty);
        studentRepository.save(student);
    }

    public void resetFaculty(long studentId) {
        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.setFaculty(null);
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> findStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> findStudentsByAgeBetween(int fromAge, int toAge) {
        return studentRepository.findByAgeBetween(fromAge, toAge);
    }

    /**
     * @return количество студентов
     */
    public long getCountOfStudents() {
        return studentRepository.getCountOfStudents();
    }

    /**
     * @return средний возраст студентов
     */
    public double getAverageAgeOfStudents() {
        return studentRepository.getAverageAgeOfStudents();
    }

    /**
     * @return список студентов из limit элементов с наибольшим id
     */
    public List<Student> getLastStudentsById(int limit) {
        return studentRepository.getLastStudentsById(limit);
    }
}
