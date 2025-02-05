package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.tools.LogEx;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public Student addStudent(Student student) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, student);

        if (student == null) {
            throw new NullStudentException();
        }
        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new StudentAlreadyExistsException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return studentRepository.save(student);
    }

    public Student getStudent(long id) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "id = " + id);
        return studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);
    }

    public Student updateStudent(Student student) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, student);

        if (student == null) {
            throw new NullStudentException();
        }
        if (studentRepository.findById(student.getId()).isEmpty()) {
            throw new StudentNotFoundException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "id = " + id);

        final Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isEmpty()) {
            throw new StudentNotFoundException();
        }

        studentRepository.deleteById(id);
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public void setFaculty(long studentId, Faculty faculty) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "studentId = " + studentId, faculty);

        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.setFaculty(faculty);
        studentRepository.save(student);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public void resetFaculty(long studentId) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "studentId = " + studentId);

        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        student.setFaculty(null);
        studentRepository.save(student);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public List<Student> getAllStudents() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN);
        return studentRepository.findAll();
    }

    public List<Student> findStudentsByAge(int age) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "age = " + age);
        return studentRepository.findByAge(age);
    }

    public List<Student> findStudentsByAgeBetween(int fromAge, int toAge) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN,
                "fromAge = " + fromAge + ", toAge = " + toAge);
        return studentRepository.findByAgeBetween(fromAge, toAge);
    }

    /**
     * @return количество студентов
     */
    public long getCountOfStudents() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN);
        return studentRepository.getCountOfStudents();
    }

    /**
     * @return средний возраст студентов
     */
    public double getAverageAgeOfStudents() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN);
        return studentRepository.getAverageAgeOfStudents();
    }

    /**
     * @return список студентов из limit элементов с наибольшим id
     */
    public List<Student> getLastStudentsById(int limit) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "limit = " + limit);
        return studentRepository.getLastStudentsById(limit);
    }
}
