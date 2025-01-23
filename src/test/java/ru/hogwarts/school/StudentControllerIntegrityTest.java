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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Objects;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class StudentControllerIntegrityTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final int port;
    private final String url;

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    private final StudentController studentController;

    private final Random random = new Random();

    StudentControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired StudentController studentController) {
        this.port = port;
        this.url = "http://localhost:" + port + "/school/student";

        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;

        this.studentController = studentController;
    }

    final Student[] students = new Student[]{
            new Student(700, "John Doe", 18, null),
            new Student(701, "Jane Doe", 19, null),
            new Student(702, "John Smith", 20, null)
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
        Assertions.assertThat(port).isPositive();
        Assertions.assertThat(url).isNotBlank();
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    @DisplayName("Добавление студента -> студент добавлен")
    void whenAddStudent_thenReturnsExpectedStudent() {

        final Student student = students[0];
        student.setId(random.nextLong());

        final String json = buildJson(student);

        final var response = restTemplate.postForObject(url, json, String.class);

        Assertions.assertThat(response.;
    }
}
