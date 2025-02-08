package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.NameGenerator;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.tools.LogEx;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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

        // Это неверный способ.
        // Для повышения производительности надо считать среднее по каждому куску стрима,
        // и затем считать среднее от суммы таких средних.
        // Я не знаю, как это сделать.
        return studentRepository.findAll()
                .parallelStream()
                .mapToInt(Student::getAge)
                .average().orElse(0.0);
    }

    /**
     * @return список студентов из limit элементов с наибольшим id
     */
    public List<Student> getLastStudentsById(int limit) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "limit = " + limit);
        return studentRepository.getLastStudentsById(limit);
    }

    public static final int ADD_TEST_LIMIT = 100_000;
    public static final String TEST_NAME_SUFFIX = " [gen]";
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_AGE = 4;
    public static final int MAX_AGE = 180;

    public int addTestStudents(int count,
                               int minNameLength, int maxNameLength,
                               int minAge, int maxAge) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "count = " + count);

        removeTestStudents();

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

        if (minAge < MIN_AGE) {
            minAge = MIN_AGE;
        } else if (maxAge > MAX_AGE) {
            maxAge = MAX_AGE;
        }

        var random = new Random();
        for (int i = 0; i < count; i++) {
            final Student student = new Student(0L,
                    NameGenerator.getName(minNameLength, maxNameLength, null) + " " +
                            NameGenerator.getName(minNameLength, maxNameLength + maxNameLength / 5,
                                    TEST_NAME_SUFFIX),
                    random.nextInt(minAge, maxAge),
                    null);
            studentRepository.save(student);
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return count;
    }

    public void removeTestStudents() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        List<Student> students = studentRepository.findByNameSuffix(TEST_NAME_SUFFIX);
        studentRepository.deleteAll(students);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    public List<String> getStudentNames(String letter) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "letter = " + letter);

        if (letter == null || letter.isEmpty()) {
            letter = "A";
            LogEx.info(log, LogEx.getThisMethodName(), "letter -> " + letter);
        }

        final String firstLetter = letter.toUpperCase();

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return studentRepository.findAll()
                .parallelStream()
                .filter(s -> s.getName().toUpperCase().startsWith(firstLetter))
                .map(s -> s.getName().toUpperCase())
                .sorted().toList();
    }

    public static final int PRINT_CHUNK_SIZE = 2;

    private void printChunk(List<String> strings) {
        strings.forEach(System.out::println);
    }

    private synchronized void printChunkSynchronized(List<String> strings) {
        strings.forEach(System.out::println);
    }

    public String printParallel() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        final List<String> students = studentRepository.findAll().stream()
                .map(Student::getName)
                .limit(PRINT_CHUNK_SIZE * 3)
                .toList();

        if (students.size() < PRINT_CHUNK_SIZE * 3) {
            LogEx.info(log, LogEx.getThisMethodName(), "students.size() < PRINT_CHUNK_SIZE * 3");
            return "Недостаточно студентов";
        }

        printChunk(students.subList(0, PRINT_CHUNK_SIZE));

        final Thread[] threads = new Thread[2];
        threads[0] = new Thread(() -> printChunk(students.subList(2, 2 + PRINT_CHUNK_SIZE)));
        threads[1] = new Thread(() -> printChunk(students.subList(4, 4 + PRINT_CHUNK_SIZE)));
        for (Thread thread : threads) {
            thread.start();
        }

        try{
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            LogEx.error(log, LogEx.getThisMethodName(), e);
            return "Ошибка при ожидании завершения потока";
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return "Список выведен к консоль в параллельном режиме";
    }

    public String printSynchronized() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING);

        final List<String> students = studentRepository.findAll().stream()
                .map(Student::getName)
                .limit(PRINT_CHUNK_SIZE * 3)
                .toList();

        if (students.size() < PRINT_CHUNK_SIZE * 3) {
            LogEx.info(log, LogEx.getThisMethodName(), "students.size() < PRINT_CHUNK_SIZE * 3");
            return "Недостаточно студентов";
        }

        printChunkSynchronized(students.subList(0, PRINT_CHUNK_SIZE));

        final Thread[] threads = new Thread[2];
        threads[0] = new Thread(() -> printChunkSynchronized(students.subList(2, 2 + PRINT_CHUNK_SIZE)));
        threads[1] = new Thread(() -> printChunkSynchronized(students.subList(4, 4 + PRINT_CHUNK_SIZE)));
        for (Thread thread : threads) {
            thread.start();
        }

        try{
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            LogEx.error(log, LogEx.getThisMethodName(), e);
            return "Ошибка при ожидании завершения потока";
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return "Список выведен к консоль в параллельном режиме";
    }
}
