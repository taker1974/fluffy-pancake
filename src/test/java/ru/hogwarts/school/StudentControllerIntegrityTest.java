package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.http.MediaType;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class StudentControllerIntegrityTest {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentController studentController;
    private final TestRestTemplate restTemplate;

    private final String baseUrl;

    StudentControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired StudentController studentController,
                                   @Autowired TestRestTemplate restTemplate) {
        baseUrl = "http://localhost:" + port + "/school/student";

        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;

        this.studentController = studentController;
        this.restTemplate = restTemplate;
    }

    private final Student[] students = new Student[]{
            new Student(0, "John Doe", 18, null),
            new Student(0, "Jane Doe", 19, null),
            new Student(0, "John Smith", 20, null)
    };

    final long wrongId = 45334L;

    String buildJson(Student student) {
        try {
            return new JSONObject()
                    .put("id", student.getId())
                    .put("name", student.getName())
                    .put("age", student.getAge()).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("Валидация контекста")
    void contextLoads() {
        Assertions.assertThat(baseUrl).isNotBlank();
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    @DisplayName("Добавление студента -> студент добавлен")
    void whenAddStudent_thenReturnsExpectedStudent() {

        final Student student = students[0];
        final var entity = new HttpEntity<>(student);

        // Добавим нового студента.
        student.setId(0);
        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, entity, Student.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isPositive();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(student.getName());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(student.getAge());

        // Попробуем добавить уже существующего студента.
        student.setId(response.getBody().getId());
        ResponseEntity<ErrorResponse> errorResponse = restTemplate.postForEntity(baseUrl, entity, ErrorResponse.class);

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code())
                .isEqualTo(StudentAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение студента -> студент получен")
    void whenGetStudent_thenReturnsExpectedStudent() throws Exception {

        final Student student = students[0];

        // TODO Найдём студента.
        // TODO Не найдём студента
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен")
    void whenUpdateStudent_thenReturnsUpdatedStudent() throws Exception {

        final Student student = students[0];
        final Student studentUpdated = new Student(
                student.getId(),
                student.getName() + " updated",
                student.getAge() + 2,
                null);

        // TODO Обновим существующего студента.

        // TODO Обновим несуществующего студента.
    }

    @Test
    @DisplayName("Удаление студента -> удалённый студент получен в последний раз")
    void whenDeleteStudent_thenReturnsDeletedStudent() throws Exception {

        final Student student = students[0];

        // TODO Удалим существующего студента.

        // TODO Удалим несуществующего студента.
    }

    @Test
    @DisplayName("Установка факультета -> студент с установленным факультетом получен")
    void whenSetFaculty_thenReturnsStudent() throws Exception {

        final Student student = students[0];
        student.setFaculty(null);
        final Faculty faculty = new Faculty(900, "Факультет 1", "Вечно синие", null);

        // TODO Установим факультет по id.

        // См. тесты WebMvcTest:
        // почему в этом случае я не вижу $.id и $.name?
        // какой метод лучше применять, если я меняю объект частично
        // через запрос по адресу типа /student/{studentId}/faculty/{facultyId}?

        // TODO Получим ошибку.
    }

    @Test
    @DisplayName("Получение факультета -> факультет получен")
    void whenGetFaculty_thenReturnsFaculty() throws Exception {

        final Student student = students[0];
        final var set = new HashSet<>(List.of(student));
        student.setFaculty(new Faculty(1, "Факультет 1", "Вечно синие", set));

        // TODO Получим объект факультета.

        // TODO Не получим факультет потому, что он не установлен.
        student.setFaculty(null);

        // TODO Получим ошибку.
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
