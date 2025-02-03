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
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import static ru.hogwarts.school.tools.StringEx.replace;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class FacultyControllerIntegrityTest extends SchoolControllerBaseTest {

    private final FacultyController facultyController;
    private final FacultyRepository facultyRepository;

    private final StudentRepository studentRepository;

    private final TestRestTemplate rest;

    private final String facultyUrl;

    FacultyControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired FacultyController facultyController,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired TestRestTemplate restTemplate) {

        facultyUrl = "http://localhost:" + port + "/school/faculty";

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
        Assertions.assertThat(facultyUrl).isNotBlank();
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    @DisplayName("Добавление факультета -> возвращается id факультета")
    void whenAddFaculty_thenReturnsFacultyId() {

        final Faculty faculty = getNew(faculties[0]);
        ResponseEntity<Long> addResponse = rest.postForEntity(
                facultyUrl + "/add",
                new HttpEntity<>(faculty), Long.class);

        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        faculty.setId(addResponse.getBody());
        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                facultyUrl + "/add",
                HttpMethod.POST, new HttpEntity<>(Objects.requireNonNull(faculty)), ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.CONFLICT, FacultyAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение факультета -> факультет получен")
    void whenGetFaculty_thenReturnsExpectedFaculty() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));

        ResponseEntity<Faculty> getResponse = rest.getForEntity(
                facultyUrl + "/" + faculty.getId(),
                Faculty.class);

        assertResponse(getResponse, faculty);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.getForEntity(
                facultyUrl + "/" + BAD_ID,
                ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление факультета -> обновлённый факультет получен")
    void whenUpdateFaculty_thenReturnsUpdatedFaculty() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));

        faculty.setName(faculty.getName() + ": updated name");
        rest.exchange(
                facultyUrl + "/update",
                HttpMethod.PUT, new HttpEntity<>(faculty), Faculty.class);

        ResponseEntity<Faculty> updatedResponse = rest.getForEntity(
                facultyUrl + "/" + faculty.getId(),
                Faculty.class);

        assertResponse(updatedResponse, faculty);

        faculty.setId(BAD_ID);
        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                facultyUrl + "/update",
                HttpMethod.PUT, new HttpEntity<>(faculty), ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление факультета -> возвращается статус NO_CONTENT")
    void whenDeleteFaculty_thenReturnsStatusNoContent() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));

        ResponseEntity<Void> deleteResponse = rest.exchange(
                facultyUrl + "/delete/" + faculty.getId(),
                HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(deleteResponse).isNotNull();
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                facultyUrl + "/delete/" + BAD_ID,
                HttpMethod.DELETE, null, ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Поиск факультетов по цвету -> список факультетов получен")
    void whenFindFacultiesByColorExact_thenReturnsFacultyOfColor() {

        Assertions.assertThat(faculties).hasSizeGreaterThanOrEqualTo(3);

        final String[] colors = new String[faculties.length];
        int baseColor = 7;
        for (int i = 0; i < colors.length; i++) {
            colors[i] = "Ice " + baseColor++;
        }
        final Iterator<String> colorIterator = (Arrays.asList(colors)).iterator();

        Arrays.stream(faculties).forEach(faculty ->
                facultyRepository.save(new Faculty(0L, faculty.getName(), colorIterator.next(), null)));

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(
                facultyUrl + "/filter/color?color=" + colors[0],
                Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);

        final ResponseEntity<Faculty[]> arrayResponseMiss = rest.getForEntity(
                facultyUrl + "/filter/color?color=" + "strange color",
                Faculty[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Поиск факультетов по названию или по цвету без учёта регистра -> список факультетов получен")
    void whenFindFacultiesByNameOrColor_thenReturnsFaculties() {

        Assertions.assertThat(faculties).hasSizeGreaterThanOrEqualTo(3);

        final String[] names = new String[faculties.length];
        final String[] colors = new String[faculties.length];

        int baseSuffix = 7;
        for (int i = 0; i < colors.length; i++) {
            names[i] = "Monsters " + baseSuffix;
            colors[i] = "Ice " + baseSuffix;
            baseSuffix++;
        }
        final Iterator<String> nameIterator = (Arrays.asList(names)).iterator();
        final Iterator<String> colorIterator = (Arrays.asList(colors)).iterator();

        Arrays.stream(faculties).forEach(_ ->
                facultyRepository.save(new Faculty(0L, nameIterator.next(), colorIterator.next(), null)));

        final ResponseEntity<Faculty[]> arrayResponseAll = rest.getForEntity(
                replace("{facultyUrl}/filter?name={name}&color={color}",
                        facultyUrl, names[0], colors[1]),
                Faculty[].class);

        Assertions.assertThat(arrayResponseAll).isNotNull();
        Assertions.assertThat(arrayResponseAll.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseAll.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponseAll.getBody()).hasSize(2);

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(
                replace("{facultyUrl}/filter?name={name}&color={color}",
                        facultyUrl, "monsTerS 7", "iCe 9"),
                Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(2);

        final ResponseEntity<Faculty[]> arrayResponseMiss = rest.getForEntity(
                replace("{facultyUrl}/filter?name={name}&color={color}",
                        facultyUrl, "bip", "pip"),
                Faculty[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Получение списка всех факультетов -> полный список факультетов получен")
    void whenGetAllFaculties_thenReturnsAllFaculties() {

        final ResponseEntity<Faculty[]> emptyArrayResponse = rest.getForEntity(facultyUrl, Faculty[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        Assertions.assertThat(faculties).hasSizeGreaterThanOrEqualTo(2);

        final String[] names = new String[faculties.length];
        final String[] colors = new String[faculties.length];

        int baseSuffix = 7;
        for (int i = 0; i < colors.length; i++) {
            names[i] = "Monsters " + baseSuffix;
            colors[i] = "Ice " + baseSuffix;
            baseSuffix++;
        }
        final Iterator<String> nameIterator = (Arrays.asList(names)).iterator();
        final Iterator<String> colorIterator = (Arrays.asList(colors)).iterator();

        Arrays.stream(faculties).forEach(_ ->
                facultyRepository.save(new Faculty(0L, nameIterator.next(), colorIterator.next(), null)));

        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(facultyUrl, Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(faculties.length);
    }

    @Test
    @DisplayName("Получение студентов факультета -> список студентов получен")
    void whenGetStudents_thenReturnsStudentsOfFaculty() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));

        final ResponseEntity<Student[]> emptyArrayResponse = rest.getForEntity(
                replace("{facultyUrl}/{facultyId}/students",
                        facultyUrl, faculty.getId()),
                Student[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(2);

        Arrays.stream(students).forEach(student -> {
            final Student newStudent = getNew(student);
            newStudent.setFaculty(faculty);
            studentRepository.save(newStudent);
        });

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                replace("{facultyUrl}/{facultyId}/students",
                        facultyUrl, faculty.getId()),
                Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(students.length);
    }
}
