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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class FacultyControllerIntegrityTest extends SchoolControllerBaseTest {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final FacultyController facultyController;
    private final TestRestTemplate rest;

    private final String baseUrl;

    FacultyControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyController facultyController,
                                   @Autowired TestRestTemplate restTemplate) {

        baseUrl = "http://localhost:" + port;

        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;

        this.facultyController = facultyController;
        this.rest = restTemplate;
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
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    @DisplayName("Добавление факультета -> возвращается id факультета")
    void whenAddFaculty_thenReturnsFacultyId() {

        final String url = baseUrl + "/school/faculty/add";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url, new HttpEntity<>(faculty), Long.class);

        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        faculty.setId(addResponse.getBody());
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url, HttpMethod.POST,
                new HttpEntity<>(Objects.requireNonNull(faculty)), ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.CONFLICT, FacultyAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение факультета -> факультет получен")
    void whenGetFaculty_thenReturnsExpectedFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(faculty), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        faculty.setId(addResponse.getBody());

        ResponseEntity<Faculty> getResponse = rest.getForEntity(url + "/" + faculty.getId(), Faculty.class);
        assertResponse(getResponse, faculty);

        ResponseEntity<ErrorResponse> errorResponse = rest.getForEntity(url + "/" + BAD_ID, ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление факультета -> обновлённый факультет получен")
    void whenUpdateFaculty_thenReturnsUpdatedFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(faculty), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        faculty.setId(addResponse.getBody());

        ResponseEntity<Faculty> getResponse = rest.getForEntity(url + "/" + faculty.getId(), Faculty.class);
        assertResponse(getResponse, faculty);

        faculty.setName(faculty.getName() + " +50% for free!");
        rest.exchange(url + "/update", HttpMethod.PUT, new HttpEntity<>(faculty), Faculty.class);

        ResponseEntity<Faculty> updatedResponse = rest.getForEntity(url + "/" + faculty.getId(), Faculty.class);
        assertResponse(updatedResponse, faculty);

        faculty.setId(BAD_ID);
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/update", HttpMethod.PUT,
                new HttpEntity<>(faculty), ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление факультета -> возвращается статус NO_CONTENT")
    void whenDeleteFaculty_thenReturnsStatusNoContent() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(faculty), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        faculty.setId(addResponse.getBody());

        ResponseEntity<Faculty> getResponse = rest.getForEntity(url + "/" + faculty.getId(), Faculty.class);
        assertResponse(getResponse, faculty);

        ResponseEntity<Void> deleteResponse = rest.exchange(url + "/delete/" + faculty.getId(),
                HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(deleteResponse).isNotNull();
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/delete/" + BAD_ID,
                HttpMethod.DELETE, null, ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Поиск факультетов по цвету -> список факультетов получен")
    void whenFindFacultiesByColorExact_thenReturnsFacultyOfColor() {

        final String url = baseUrl + "/school/faculty";

        final String[] colors = new String[faculties.length];
        int baseColor = 7;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = "Ice " + baseColor++;
        }
        final Iterator<String> colorsIterator = (Arrays.asList(colors)).iterator();

        Arrays.stream(faculties).forEach(faculty -> {
            Faculty newFaculty = new Faculty(faculty).setNew();
            newFaculty.setColor(colorsIterator.next());
            rest.postForEntity(url + "/add",
                    new HttpEntity<>(newFaculty), Long.class);
        });

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(
                url + "/filter/color?color=" + colors[0], Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);

        final ResponseEntity<Faculty[]> arrayResponseMiss = rest.getForEntity(
                url + "/filter/color?color=" + "strange color", Faculty[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Поиск факультетов по названию или по цвету без учёта регистра -> список факультетов получен")
    void whenFindFacultiesByNameOrColor_thenReturnsFaculties() {

        final String url = baseUrl + "/school/faculty";

        final String[] names = new String[faculties.length];
        final String[] colors = new String[faculties.length];

        int baseSuffix = 7;

        for (int i = 0; i < colors.length; i++) {
            names[i] = "Monsters " + baseSuffix;
            colors[i] = "Ice " + baseSuffix;
            baseSuffix++;
        }
        final Iterator<String> namessIterator = (Arrays.asList(names)).iterator();
        final Iterator<String> colorsIterator = (Arrays.asList(colors)).iterator();

        Arrays.stream(faculties).forEach(faculty -> {
            Faculty newFaculty = new Faculty(faculty).setNew();
            newFaculty.setName(namessIterator.next());
            newFaculty.setColor(colorsIterator.next());
            rest.postForEntity(url + "/add",
                    new HttpEntity<>(newFaculty), Long.class);
        });

        final ResponseEntity<Faculty[]> arrayResponseAll = rest.getForEntity(
                String.format("%s", url), Faculty[].class);
        Assertions.assertThat(arrayResponseAll).isNotNull();
        Assertions.assertThat(arrayResponseAll.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseAll.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponseAll.getBody()).hasSize(faculties.length);

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(
                String.format("%s/filter?name=%s&color=%s", url, "monsTerS 7", "iCe 9"), Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(2);

        final ResponseEntity<Faculty[]> arrayResponseMiss = rest.getForEntity(
                String.format("%s/filter?name=%s&color=%s", url, "bip", "pip"), Faculty[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Получение списка всех факультетов -> полный список факультетов получен")
    void whenGetAllFaculties_thenReturnsAllFaculties() {

        final String url = baseUrl + "/school/faculty";

        final ResponseEntity<Faculty[]> emptyArrayResponse = rest.getForEntity(url, Faculty[].class);
        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        Arrays.stream(faculties).forEach(faculty ->{
                Faculty newFaculty = new Faculty(faculty).setNew();
                rest.postForEntity(url + "/add", new HttpEntity<>(newFaculty), Long.class);
        });

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(url, Faculty[].class);
        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(faculties.length);
    }

    @Test
    @DisplayName("Получение студентов факультета -> список студентов получен")
    void whenGetStudents_thenReturnsStudentsOfFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addFacultyResponse = rest.postForEntity(url + "/add", new HttpEntity<>(faculty), Long.class);
        Assertions.assertThat(addFacultyResponse).isNotNull();
        Assertions.assertThat(addFacultyResponse.getBody()).isNotNull();

        faculty.setId(addFacultyResponse.getBody());

        Arrays.stream(students).forEach(student -> {
            Student newStudent = new Student(student).setNew();
            newStudent.setFaculty(faculty);
            rest.postForEntity(baseUrl + "/school/student/add", new HttpEntity<>(student), Long.class);
        });

        rest.postForEntity(url + "/add", new HttpEntity<>(new Faculty(faculties[1]).setNew()), Long.class);
        rest.postForEntity(url + "/add", new HttpEntity<>(new Faculty(faculties[2]).setNew()), Long.class);

        final ResponseEntity<Faculty[]> facultiesResponse = rest.getForEntity(url, Faculty[].class);
        Assertions.assertThat(facultiesResponse).isNotNull();
        Assertions.assertThat(facultiesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(facultiesResponse.getBody()).isNotEmpty();
        Assertions.assertThat(facultiesResponse.getBody()).hasSize(3);

        Arrays.stream(facultiesResponse.getBody())
                .filter(Objects::nonNull)
                .filter(x -> x.getStudents() != null)
                .forEach(x ->
                        Assertions.assertThat(x.getStudents()).hasSize(students.length));
    }
}
