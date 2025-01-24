package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class StudentControllerIntegrityTest extends SchoolControllerBaseTest {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentController studentController;
    private final TestRestTemplate restTemplate;

    private final String studentApiUrl;
    private final String facultyApiUrl;

    StudentControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired StudentController studentController,
                                   @Autowired TestRestTemplate restTemplate) {

        studentApiUrl = "http://localhost:" + port + "/school/student";
        facultyApiUrl = "http://localhost:" + port + "/school/faculty";

        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;

        this.studentController = studentController;
        this.restTemplate = restTemplate;
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Валидация контекста")
    void contextLoads() {
        Assertions.assertThat(studentApiUrl).isNotBlank();
        Assertions.assertThat(facultyApiUrl).isNotBlank();
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    @DisplayName("Добавление студента -> студент добавлен")
    void whenAddStudent_thenReturnsExpectedStudent() {

        final Student student = students[0];
        ResponseEntity<Student> addResponse = addNew(restTemplate,
                studentApiUrl, student);

        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(addResponse.getBody()).isNotNull();
        Assertions.assertThat(addResponse.getBody().getId()).isPositive();
        Assertions.assertThat(addResponse.getBody().getName()).isEqualTo(student.getName());

        // Попробуем добавить уже существующего студента.
        ResponseEntity<ErrorResponse> errorResponse = addSame(restTemplate,
                studentApiUrl, student, addResponse.getBody().getId());

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code())
                .isEqualTo(StudentAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение студента -> студент получен")
    void whenGetStudent_thenReturnsExpectedStudent() {

        final Student student = students[0];
        ResponseEntity<Student> addResponse = addNew(restTemplate,
                studentApiUrl, student);

        final long assignedId = Objects.requireNonNull(addResponse.getBody()).getId();

        // Получим добавленного студента.
        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                String.format("%s/%d", studentApiUrl, assignedId), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(assignedId);
        Assertions.assertThat(getResponse.getBody().getName()).isEqualTo(student.getName());

        // Запросим сведения о несуществующем студенте.
        ResponseEntity<ErrorResponse> errorResponse = restTemplate.getForEntity(
                String.format("%s/%d", studentApiUrl, wrongId), ErrorResponse.class);

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code())
                .isEqualTo(StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен")
    void whenUpdateStudent_thenReturnsUpdatedStudent() {

        final Student student = students[0];
        addNew(restTemplate, studentApiUrl, student);

        // Обновим существующего студента.
        final Student updatedStudent = new Student(
                student.getId(),
                student.getName() + " updated",
                student.getAge() + 2,
                null);

        var entity = new HttpEntity<>(updatedStudent);
        restTemplate.exchange(
                studentApiUrl, HttpMethod.PUT, entity, Student.class);

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                String.format("%s/%d", studentApiUrl, updatedStudent.getId()), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(updatedStudent.getId());
        Assertions.assertThat(getResponse.getBody().getName()).isEqualTo(updatedStudent.getName());

        // Обновим несуществующего студента.
        updatedStudent.setId(wrongId);
        entity = new HttpEntity<>(updatedStudent);
        final ResponseEntity<ErrorResponse> errorResponse = restTemplate.exchange(
                studentApiUrl, HttpMethod.PUT, entity, ErrorResponse.class);

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code())
                .isEqualTo(StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление студента -> удалённый студент получен в последний раз")
    void whenDeleteStudent_thenReturnsDeletedStudent() {

        final Student student = students[0];
        ResponseEntity<Student> addResponse = addNew(restTemplate,
                studentApiUrl, student);

        long assignedId = Objects.requireNonNull(addResponse.getBody()).getId();

        // Удалим существующего студента.
        ResponseEntity<Student> deleteResponse = restTemplate.exchange(
                String.format("%s/%d", studentApiUrl, assignedId), HttpMethod.DELETE, null,
                Student.class);

        Assertions.assertThat(deleteResponse).isNotNull();
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(deleteResponse.getBody()).isNotNull();
        Assertions.assertThat(deleteResponse.getBody().getId()).isEqualTo(assignedId);
        Assertions.assertThat(deleteResponse.getBody().getName()).isEqualTo(student.getName());

        // Удалим несуществующего студента.
        ResponseEntity<ErrorResponse> errorResponse = restTemplate.exchange(
                String.format("%s/%d", studentApiUrl, wrongId), HttpMethod.DELETE, null,
                ErrorResponse.class);

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code())
                .isEqualTo(StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Установка факультета студента -> студент с установленным факультетом получен")
    void whenSetFaculty_thenReturnsStudent() {

        final Student student = students[0];
        student.setFaculty(null);
        ResponseEntity<Student> addResponse = addNew(restTemplate,
                studentApiUrl, student);
        final Student newStudent = addResponse.getBody();

        Assertions.assertThat(newStudent).isNotNull();

        final Faculty faculty = faculties[0];
        ResponseEntity<Faculty> addFacultyResponse = addNew(restTemplate,
                facultyApiUrl, faculty);

        final Faculty newFaculty = Objects.requireNonNull(addFacultyResponse.getBody());

        // Прикрепим студента к факультету.
        newStudent.setFaculty(newFaculty);
        HttpEntity<Student> studentEntity = new HttpEntity<>(student);
        restTemplate.exchange(
                studentApiUrl, HttpMethod.PUT, studentEntity, Student.class);

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                String.format("%s/%d", studentApiUrl, newStudent.getId()), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(newStudent.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(newFaculty.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty().getName()).isEqualTo(newFaculty.getName());

        // Получим ошибку.
        // Попытаемся установить факультет, которого нет в базе.
        final Faculty badFaculty = faculties[1];
        badFaculty.setId(wrongId);

        newStudent.setFaculty(badFaculty);

        studentEntity = new HttpEntity<>(newStudent);
        ResponseEntity<Student> badStudentResponse = restTemplate.exchange(
                studentApiUrl, HttpMethod.PUT, studentEntity, Student.class);

        Assertions.assertThat(badStudentResponse).isNotNull();
    }

    @Test
    @DisplayName("Получение факультета студента -> факультет получен")
    void whenGetFaculty_thenReturnsFaculty() throws Exception {

        final Student student = students[0];
        final ResponseEntity<Student> addResponse = addNew(restTemplate,
                studentApiUrl, student);

        final Faculty faculty = faculties[0];
        final ResponseEntity<Faculty> addFacultyResponse = addNew(restTemplate,
                facultyApiUrl, faculty);

        Objects.requireNonNull(addResponse.getBody())
                .setFaculty(addFacultyResponse.getBody());

        final HttpEntity<Student> studentEntity = new HttpEntity<>(addResponse.getBody());
        ResponseEntity<Student> updateResponse = restTemplate.exchange(studentApiUrl,
                HttpMethod.PUT, studentEntity, Student.class);

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                String.format("%s/%d/faculty",
                        studentApiUrl, Objects.requireNonNull(updateResponse.getBody()).getId()),
                Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(addResponse.getBody().getId());
        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId())
                .isEqualTo(Objects.requireNonNull(addFacultyResponse.getBody()).getId());
    }

    @Test
    @DisplayName("Сброс факультета -> обновлённый студент получен")
    void whenResetFaculty_thenReturnsStudent() throws Exception {

        final Student student = students[0];
        final var set = new HashSet<>(List.of(student));

        student.setFaculty(new Faculty(1, "Факультет 1", "Вечно синие", set));

        // TODO Получим объект факультета.

        // TODO Не получим факультет потому, что он не установлен.
        student.setFaculty(null);

        // TODO Получим ошибку (не найдём студента).
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() throws Exception {

        // TODO Получим объекты всех студентов.

        // TODO Получим пустой список студентов.
    }

    @Test
    @DisplayName("Поиск студентов по точному возрасту -> список студентов получен")
    void whenFindStudentsByAge_thenReturnsStudentsOfAge() throws Exception {

        // TODO Получим список всех студентов искомого возраста.
        final Student[] sameAgeStudents = new Student[]{students[0], students[1]};

        // TODO Получим пустой список студентов.
    }

    @Test
    @DisplayName("Поиск студентов по диапазону возраста -> список студентов получен")
    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() throws Exception {

        // TODO Получим список студентов по диапазону возраста.
        final Student[] ageInRangeStudents = new Student[]{students[1], students[2]};

        // TODO Получим пустой список студентов.
    }

    // См. тесты WebMvcTest:
    // Реальное приложение отвечает на этот запрос валидным JSON для Student.
    // В ответе mock нет ничего, кроме статуса.
    private void handleResult(MvcResult result) {
        try {
            final var response = result.getResponse();
            final var contentType = response.getContentType();
            final var content = response.getContentAsString();

            System.out.printf("Content type: %s;%nContent: %s%n", contentType, content);
        } catch (IOException e) {
            org.junit.jupiter.api.Assertions.fail();
        }
    }

}
