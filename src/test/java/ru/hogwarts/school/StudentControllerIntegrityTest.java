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
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.controller.advice.CommonControllerAdvice;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-h2")
class StudentControllerIntegrityTest extends SchoolControllerBaseTest {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentController studentController;
    private final TestRestTemplate rest;

    private final String baseUrl;

    StudentControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired StudentController studentController,
                                   @Autowired TestRestTemplate restTemplate) {

        baseUrl = "http://localhost:" + port;

        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;

        this.studentController = studentController;
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
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    @DisplayName("Добавление студента -> возвращается id нового студента")
    void whenAddStudent_thenReturnsStudentId() {

        final String url = baseUrl + "/school/student/add";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> response = rest.postForEntity(url, new HttpEntity<>(student), Long.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();

        student.setId(response.getBody());
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url, HttpMethod.POST,
                new HttpEntity<>(Objects.requireNonNull(student)), ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.CONFLICT, StudentAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение студента -> студент получен")
    void whenGetStudent_thenReturnsExpectedStudent() {

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(student), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        student.setId(addResponse.getBody());

        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + student.getId(), Student.class);
        assertResponse(getResponse, student);

        ResponseEntity<ErrorResponse> errorResponse = rest.getForEntity(url + "/" + BAD_ID, ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен")
    void whenUpdateStudent_thenReturnsUpdatedStudent() {

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(student), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        student.setId(addResponse.getBody());

        final Student updatedStudent = new Student(student);
        updatedStudent.setName(student.getName() + " +50% for free!");
        updatedStudent.setAge(student.getAge() + 2);

        rest.exchange(url + "/update", HttpMethod.PUT, new HttpEntity<>(updatedStudent), Student.class);

        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + updatedStudent.getId(), Student.class);
        assertResponse(getResponse, updatedStudent);

        updatedStudent.setId(BAD_ID);
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/update", HttpMethod.PUT,
                new HttpEntity<>(updatedStudent), ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление студента -> возвращается статус NO_CONTENT")
    void whenDeleteStudent_thenReturnsStatusNoContent() {

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addResponse = rest.postForEntity(url + "/add", new HttpEntity<>(student), Long.class);
        Assertions.assertThat(addResponse).isNotNull();
        Assertions.assertThat(addResponse.getBody()).isNotNull();

        student.setId(addResponse.getBody());

        ResponseEntity<Void> deleteResponse = rest.exchange(url + "/delete/" + student.getId(),
                HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(deleteResponse).isNotNull();
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/delete/" + BAD_ID,
                HttpMethod.DELETE, null, ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Установка факультета студента по Id -> студент с установленным факультетом получен")
    void whenSetStudentFaculty_thenReturnsStudentWithFaculty() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addFacultyResponse = rest.postForEntity(facultyUrl + "/add",
                new HttpEntity<>(faculty), Long.class);

        Assertions.assertThat(addFacultyResponse.getBody()).isNotNull();
        faculty.setId(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addStudentResponse = rest.postForEntity(url + "/add",
                new HttpEntity<>(student), Long.class);

        Assertions.assertThat(addStudentResponse.getBody()).isNotNull();
        student.setId(addStudentResponse.getBody());

        final String linkageUrl = url + "/" + student.getId() + "/faculty/" + faculty.getId();

        rest.exchange(linkageUrl, HttpMethod.PATCH, null, Void.class);

        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + student.getId(), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(student.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(faculty.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty().getName()).isEqualTo(faculty.getName());

        final String badLinkageUrl = url + "/" + student.getId() + "/faculty/" + BAD_ID;

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(badLinkageUrl, HttpMethod.PATCH,
                null, ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Получение факультета студента -> факультет получен")
    void whenGetStudentFaculty_thenReturnsFaculty() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addFacultyResponse = rest.postForEntity(facultyUrl + "/add",
                new HttpEntity<>(faculty), Long.class);

        Assertions.assertThat(addFacultyResponse.getBody()).isNotNull();
        faculty.setId(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addStudentResponse = rest.postForEntity(url + "/add",
                new HttpEntity<>(student), Long.class);

        Assertions.assertThat(addStudentResponse.getBody()).isNotNull();
        student.setId(addStudentResponse.getBody());

        student.setFaculty(faculty);
        ResponseEntity<Student> updateResponse = rest.exchange(url + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), Student.class);

        Assertions.assertThat(updateResponse).isNotNull();
        Assertions.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(updateResponse.getBody()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getId()).isEqualTo(student.getId());
        Assertions.assertThat(updateResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getFaculty().getId()).isEqualTo(faculty.getId());

        final Faculty facultyNotInDb = new Faculty(faculties[1]).setNew();
        student.setFaculty(facultyNotInDb);

        ResponseEntity<ErrorResponse> badUpdateResponse = rest.exchange(url + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), ErrorResponse.class);

        Assertions.assertThat(badUpdateResponse).isNotNull();
        Assertions.assertThat(badUpdateResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(badUpdateResponse.getBody()).isNotNull();
        Assertions.assertThat(badUpdateResponse.getBody().code()).isEqualTo(CommonControllerAdvice.RTE_CODE);
    }

    @Test
    @DisplayName("Сброс факультета студента -> возвращается статус OK")
    void whenResetStudentFaculty_thenReturnsStatusOK() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = new Faculty(faculties[0]).setNew();

        ResponseEntity<Long> addFacultyResponse = rest.postForEntity(facultyUrl + "/add",
                new HttpEntity<>(faculty), Long.class);

        Assertions.assertThat(addFacultyResponse.getBody()).isNotNull();
        faculty.setId(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = new Student(students[0]).setNew();

        ResponseEntity<Long> addStudentResponse = rest.postForEntity(url + "/add",
                new HttpEntity<>(student), Long.class);

        Assertions.assertThat(addStudentResponse.getBody()).isNotNull();
        student.setId(addStudentResponse.getBody());

        student.setFaculty(faculty);
        ResponseEntity<Student> updateResponse = rest.exchange(url + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), Student.class);

        Assertions.assertThat(updateResponse).isNotNull();
        Assertions.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(updateResponse.getBody()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getId()).isEqualTo(student.getId());
        Assertions.assertThat(updateResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getFaculty().getId()).isEqualTo(faculty.getId());

        rest.exchange(url + "/" + student.getId() + "/faculty/reset",
                HttpMethod.PATCH, null, Void.class);

        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + student.getId(), Student.class);
        assertResponse(getResponse, student);
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() {

        final String url = baseUrl + "/school/student";

        final ResponseEntity<Student[]> emptyArrayResponse = rest.getForEntity(url, Student[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        Arrays.stream(students).forEach(student ->
                rest.postForEntity(url + "/add",
                        new HttpEntity<>(new Student(student).setNew()),
                        Long.class));

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(url, Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(students.length);
    }

    @Test
    @DisplayName("Поиск студентов по точному возрасту -> список студентов получен")
    void whenFindStudentsByAgeExact_thenReturnsStudentsOfAge() {

        final String url = baseUrl + "/school/student";

        final Integer[] ages = new Integer[students.length];
        int baseAge = 17;
        for (int i = 0; i < ages.length; i++) {
            ages[i] = baseAge++;
        }
        final Iterator<Integer> agesIterator = (Arrays.asList(ages)).iterator();

        Arrays.stream(students).forEach(student -> {

            Student newStudent = new Student(student).setNew();
            newStudent.setAge(agesIterator.next());

            rest.postForEntity(url + "/add",
                    new HttpEntity<>(newStudent),
                    Long.class);
        });

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(url + "/filter/age/" + ages[0],
                Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);

        final ResponseEntity<Student[]> arrayResponseMiss = rest.getForEntity(url + "/filter/age/666",
                Student[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Поиск студентов по диапазону возраста -> список студентов получен")
    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() {

        final String url = baseUrl + "/school/student";

        final Integer[] ages = new Integer[students.length];
        int baseAge = 17;
        for (int i = 0; i < ages.length; i++) {
            ages[i] = baseAge++;
        }
        final Iterator<Integer> agesIterator = (Arrays.asList(ages)).iterator();

        Arrays.stream(students).forEach(student -> {

            Student newStudent = new Student(student).setNew();
            newStudent.setAge(agesIterator.next());

            rest.postForEntity(url + "/add",
                    new HttpEntity<>(newStudent),
                    Long.class);
        });

        Assertions.assertThat(students).hasSizeGreaterThan(1);

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                url + "/filter/age/between?fromAge=17&toAge=18", Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(2);

        final ResponseEntity<Student[]> arrayResponseMiss = rest.getForEntity(
                url + "/filter/age/between?fromAge=666&toAge=800", Student[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }
}
