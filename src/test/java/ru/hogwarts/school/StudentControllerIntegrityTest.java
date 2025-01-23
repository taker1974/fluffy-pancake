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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Objects;

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
}
