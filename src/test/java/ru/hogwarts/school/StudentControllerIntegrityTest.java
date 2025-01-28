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

        // Мне удобно, когда в каждом методе перед глазами
        // есть подстрока адреса запроса - так легче не запутаться.
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

        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + addResponse.getBody(), Student.class);
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

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/delete/" + student.getId(),
                HttpMethod.DELETE, null, ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Установка факультета студента по Id -> студент с установленным факультетом получен")
    void whenSetStudentFaculty_thenReturnsStudentWithFaculty() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем факультет.
        ResponseEntity<Faculty> addFacultyResponse = rest.postForEntity(facultyUrl,
                new HttpEntity<>(faculty), Faculty.class);
        final Faculty addedFaculty = Objects.requireNonNull(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = createByAnother(students[0]);

        // Добавляем нового студента.
        ResponseEntity<Student> addResponse = rest.postForEntity(url, new HttpEntity<>(student), Student.class);
        Student addedStudent = Objects.requireNonNull(addResponse.getBody());

        final String linkageUrl = url + "/" + addedStudent.getId() + "/faculty/" + addedFaculty.getId();

        // Устанавливаем факультет через идентификаторы.
        rest.exchange(linkageUrl, HttpMethod.PATCH, null, Student.class);

        // Прочитаем студента и в том числе проверим факультет.
        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + addedStudent.getId(), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(addedStudent.getId());

        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(addedFaculty.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty().getName()).isEqualTo(addedFaculty.getName());

        // Попытаемся установить несуществующий факультет.
        final String badLinkageUrl = url + "/" + addedStudent.getId() + "/faculty/" + BAD_ID;

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(badLinkageUrl, HttpMethod.PATCH,
                null, ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.BAD_REQUEST, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Получение факультета студента -> факультет получен")
    void whenGetStudentFaculty_thenReturnsFaculty() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем факультет.
        ResponseEntity<Faculty> addFacultyResponse = rest.postForEntity(facultyUrl,
                new HttpEntity<>(faculty), Faculty.class);
        final Faculty addedFaculty = Objects.requireNonNull(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = createByAnother(students[0]);
        student.setFaculty(addFacultyResponse.getBody());

        // Добавляем нового студента с уже добавленным факультетом.
        ResponseEntity<Student> addResponse = rest.postForEntity(url, new HttpEntity<>(student), Student.class);
        Student addedStudent = Objects.requireNonNull(addResponse.getBody());

        // Прочитаем студента и в том числе проверим факультет.
        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + addedStudent.getId(), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(addedStudent.getId());

        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(addedFaculty.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty().getName()).isEqualTo(addedFaculty.getName());

        // Попытаемся установить несуществующий (несуществующий в БД!) факультет.
        final Faculty notSavedFaculty = createByAnother(faculties[1]);
        addedStudent.setFaculty(notSavedFaculty);

        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url, HttpMethod.PATCH,
                new HttpEntity<>(addedStudent), ErrorResponse.class);

        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(errorResponse.getBody()).isNotNull();
        Assertions.assertThat(errorResponse.getBody().code()).isEqualTo(CommonControllerAdvice.E_CODE);
    }

    @Test
    @DisplayName("Сброс факультета студента -> обновлённый студент получен")
    void whenResetStudentFaculty_thenReturnsStudent() {

        final String facultyUrl = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем факультет.
        ResponseEntity<Faculty> addFacultyResponse = rest.postForEntity(facultyUrl,
                new HttpEntity<>(faculty), Faculty.class);
        final Faculty addedFaculty = Objects.requireNonNull(addFacultyResponse.getBody());

        final String url = baseUrl + "/school/student";
        final Student student = createByAnother(students[0]);
        student.setFaculty(addFacultyResponse.getBody());

        // Добавляем нового студента с уже добавленным факультетом.
        ResponseEntity<Student> addResponse = rest.postForEntity(url, new HttpEntity<>(student), Student.class);
        Student addedStudent = Objects.requireNonNull(addResponse.getBody());

        // Прочитаем студента и проверим факультет.
        ResponseEntity<Student> getResponse = rest.getForEntity(url + "/" + addedStudent.getId(), Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(addedStudent.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(addedFaculty.getId());

        final String deleteUrl = url + "/" + addedStudent.getId() + "/faculty/reset";

        ResponseEntity<Student> resetResponse = rest.exchange(deleteUrl, HttpMethod.PATCH,
                null, Student.class);

        Assertions.assertThat(resetResponse).isNotNull();
        Assertions.assertThat(resetResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(resetResponse.getBody()).isNotNull();
        Assertions.assertThat(resetResponse.getBody().getId()).isEqualTo(addedStudent.getId());
        Assertions.assertThat(resetResponse.getBody().getFaculty()).isNull();
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() {

        final String url = baseUrl + "/school/student";

        // Попробуем прочитать всех студентов из пустого хранилища.
        final ResponseEntity<Student[]> emptyArrayResponse = rest.getForEntity(url, Student[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        // Добавим студентов из массива.
        Arrays.stream(students).forEach(student ->
                rest.postForEntity(url, new HttpEntity<>(createByAnother(student)), Student.class));

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

        // Добавим студентов из массива.
        Arrays.stream(students).forEach(student -> {
            student.setAge(agesIterator.next());
            rest.postForEntity(url, new HttpEntity<>(createByAnother(student)), Student.class);
        });

        // Найдём студента с указанным возрастом. Он должен быть один.
        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(url + "/filter/age/" + ages[0],
                Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);
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

        // Добавим студентов из массива.
        Arrays.stream(students).forEach(student -> {
            student.setAge(agesIterator.next());
            rest.postForEntity(url, new HttpEntity<>(createByAnother(student)), Student.class);
        });

        Assertions.assertThat(students).hasSizeGreaterThan(1);

        // Найдём студентов с указанными границами возраста.
        // Пусть их будет двое.
        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                url + "/filter/age/between?fromAge=17&toAge=18", Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(2);
    }
}
