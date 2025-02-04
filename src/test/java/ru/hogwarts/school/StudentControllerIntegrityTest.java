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
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

import static ru.hogwarts.school.tools.StringEx.replace;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-pg")
class StudentControllerIntegrityTest extends SchoolControllerBaseTest {

    private final StudentController studentController;
    private final StudentRepository studentRepository;

    private final FacultyRepository facultyRepository;

    private final TestRestTemplate rest;

    private final String studentUrl;

    StudentControllerIntegrityTest(@LocalServerPort int port,
                                   @Autowired StudentController studentController,
                                   @Autowired StudentRepository studentRepository,
                                   @Autowired FacultyRepository facultyRepository,
                                   @Autowired TestRestTemplate restTemplate) {

        studentUrl = "http://localhost:" + port + "/school/student";

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
        Assertions.assertThat(studentUrl).isNotBlank();
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    @DisplayName("Добавление студента -> возвращается id нового студента")
    void whenAddStudent_thenReturnsStudentId() {

        final String urlAdd = studentUrl + "/add";

        final Student student = getNew(students[0]);
        ResponseEntity<Long> response = rest.postForEntity(urlAdd, new HttpEntity<>(student), Long.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();

        student.setId(response.getBody());
        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(urlAdd, HttpMethod.POST,
                new HttpEntity<>(Objects.requireNonNull(student)), ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.CONFLICT, StudentAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение студента -> студент получен")
    void whenGetStudent_thenReturnsExpectedStudent() {

        final Student student = studentRepository.save(getNew(students[0]));
        final String urlGet = studentUrl + "/" + student.getId();

        ResponseEntity<Student> getResponse = rest.getForEntity(urlGet, Student.class);
        assertResponse(getResponse, student);

        final String urlGetBad = studentUrl + "/" + BAD_ID;
        ResponseEntity<ErrorResponseDto> errorResponse = rest.getForEntity(urlGetBad, ErrorResponseDto.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен")
    void whenUpdateStudent_thenReturnsUpdatedStudent() {

        final Student student = studentRepository.save(getNew(students[0]));
        final String urlUpdate = studentUrl + "/update";

        final Student updatedStudent = new Student(student);
        updatedStudent.setName(student.getName() + " (updated name)");
        updatedStudent.setAge(student.getAge() + 2);

        rest.put(urlUpdate, new HttpEntity<>(updatedStudent), Student.class);

        Optional<Student> optionalStudent = studentRepository.findById(updatedStudent.getId());

        Assertions.assertThat(optionalStudent).isPresent();
        Assertions.assertThat(optionalStudent.get()).isNotNull();
        Assertions.assertThat(optionalStudent.get().getId()).isEqualTo(updatedStudent.getId());
        Assertions.assertThat(optionalStudent.get().getName()).isEqualTo(updatedStudent.getName());
        Assertions.assertThat(optionalStudent.get().getAge()).isEqualTo(updatedStudent.getAge());

        updatedStudent.setId(BAD_ID);
        updatedStudent.setName(student.getName() + " (bad name)");
        updatedStudent.setAge(student.getAge() + 6);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(urlUpdate, HttpMethod.PUT,
                new HttpEntity<>(updatedStudent), ErrorResponseDto.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление студента -> возвращается статус NO_CONTENT")
    void whenDeleteStudent_thenReturnsStatusNoContent() {

        final Student student = studentRepository.save(getNew(students[0]));
        final String urlDelete = studentUrl + "/" + student.getId() + "/delete";

        ResponseEntity<Void> deleteResponse = rest.exchange(urlDelete, HttpMethod.DELETE, null,
                Void.class);
        Assertions.assertThat(deleteResponse).isNotNull();
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(urlDelete, HttpMethod.DELETE,
                null, ErrorResponseDto.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);

        final String urlDeleteBad = studentUrl + "/" + BAD_ID + "/delete";

        errorResponse = rest.exchange(urlDeleteBad, HttpMethod.DELETE,
                null, ErrorResponseDto.class);
        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, StudentNotFoundException.CODE);
    }

    @Test
    @DisplayName("Установка факультета студента по Id -> студент с установленным факультетом получен")
    void whenSetStudentFaculty_thenReturnsStudentWithFaculty() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));
        final Student student = studentRepository.save(getNew(students[0]));

        final String linkageUrl = replace("{studentUrl}/{studentId}/faculty/{facultyId}",
                studentUrl, student.getId(), faculty.getId());

        rest.exchange(linkageUrl, HttpMethod.PATCH, null, Void.class);

        ResponseEntity<Student> getResponse = rest.getForEntity(
                studentUrl + "/" + student.getId(),
                Student.class);

        Assertions.assertThat(getResponse).isNotNull();
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getResponse.getBody()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getId()).isEqualTo(student.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(getResponse.getBody().getFaculty().getId()).isEqualTo(faculty.getId());
        Assertions.assertThat(getResponse.getBody().getFaculty().getName()).isEqualTo(faculty.getName());

        final String badLinkageUrl = replace("{studentUrl}/{studentId}/faculty/{facultyId}",
                studentUrl, student.getId(), BAD_ID);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(badLinkageUrl, HttpMethod.PATCH,
                null, ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Получение факультета студента -> факультет получен")
    void whenGetStudentFaculty_thenReturnsFaculty() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));
        final Student student = studentRepository.save(getNew(students[0]));

        student.setFaculty(faculty);
        ResponseEntity<Student> updateResponse = rest.exchange(
                studentUrl + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), Student.class);

        Assertions.assertThat(updateResponse).isNotNull();
        Assertions.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(updateResponse.getBody()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getId()).isEqualTo(student.getId());
        Assertions.assertThat(updateResponse.getBody().getFaculty()).isNotNull();
        Assertions.assertThat(updateResponse.getBody().getFaculty().getId()).isEqualTo(faculty.getId());

        final Faculty facultyNotInDb = getNew(faculties[1]);
        student.setFaculty(facultyNotInDb);

        ResponseEntity<ErrorResponseDto> badUpdateResponse = rest.exchange(
                studentUrl + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), ErrorResponseDto.class);

        Assertions.assertThat(badUpdateResponse).isNotNull();
        Assertions.assertThat(badUpdateResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(badUpdateResponse.getBody()).isNotNull();
        Assertions.assertThat(badUpdateResponse.getBody().code()).isEqualTo(CommonControllerAdvice.RTE_CODE);
    }

    @Test
    @DisplayName("Сброс факультета студента -> возвращается статус OK")
    void whenResetStudentFaculty_thenReturnsStatusOK() {

        final Faculty faculty = facultyRepository.save(getNew(faculties[0]));
        final Student student = studentRepository.save(getNew(students[0]));

        student.setFaculty(faculty);
        rest.exchange(
                studentUrl + "/update",
                HttpMethod.PUT, new HttpEntity<>(student), Student.class);

        rest.exchange(replace("{studentUrl}/{studentId}/faculty/reset",
                        studentUrl, student.getId()),
                HttpMethod.PATCH, null, Void.class);

        ResponseEntity<Student> getResponse = rest.getForEntity(
                studentUrl + "/" + student.getId(), Student.class);
        assertResponse(getResponse, student);
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() {

        final ResponseEntity<Student[]> emptyArrayResponse = rest.getForEntity(
                studentUrl, Student[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                studentUrl, Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(students.length);
    }

    @Test
    @DisplayName("Поиск студентов по точному возрасту -> список студентов получен")
    void whenFindStudentsByAgeExact_thenReturnsStudentsOfAge() {

        final Integer[] ages = new Integer[students.length];
        int baseAge = 17;
        for (int i = 0; i < ages.length; i++) {
            ages[i] = baseAge++;
        }
        final Iterator<Integer> agesIterator = (Arrays.asList(ages)).iterator();

        Arrays.stream(students).forEach(student ->
                studentRepository.save(new Student(0L, student.getName(), agesIterator.next(), null)));

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                studentUrl + "/filter/age/" + ages[0],
                Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);

        final ResponseEntity<Student[]> arrayResponseMiss = rest.getForEntity(
                studentUrl + "/filter/age/" + (baseAge + 100),
                Student[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Поиск студентов по диапазону возраста -> список студентов получен")
    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        final Integer[] ages = new Integer[students.length];
        int baseAge = 17;
        for (int i = 0; i < ages.length; i++) {
            ages[i] = baseAge++;
        }
        final Iterator<Integer> agesIterator = (Arrays.asList(ages)).iterator();

        Arrays.stream(students).forEach(student ->
                studentRepository.save(new Student(0L, student.getName(), agesIterator.next(), null)));

        final ResponseEntity<Student[]> arrayResponse = rest.getForEntity(
                replace("{studentUrl}/filter/age/between?fromAge={fromAge}&toAge={toAge}",
                        studentUrl, 17, 18),
                Student[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(2);

        final ResponseEntity<Student[]> arrayResponseMiss = rest.getForEntity(
                replace("{studentUrl}/filter/age/between?fromAge={fromAge}&toAge={toAge}",
                        studentUrl, baseAge + 100, baseAge + 200),
                Student[].class);

        Assertions.assertThat(arrayResponseMiss).isNotNull();
        Assertions.assertThat(arrayResponseMiss.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponseMiss.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Получение количества студентов -> количество получено")
    void whenGetCountOfStudents_thenReturnsExpectedCountOfStudents() {

        Arrays.stream(students).forEach(student ->
                studentRepository.save(new Student(0L, student.getName(), 17, null)));

        ResponseEntity<Long> getCountResponse = rest.getForEntity(
                studentUrl + "/stat/count",
                Long.class);

        Assertions.assertThat(getCountResponse).isNotNull();
        Assertions.assertThat(getCountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getCountResponse.getBody()).isEqualTo(students.length);
    }

    @Test
    @DisplayName("Получение количества студентов -> количество получено")
    void whenGetAverageAgeOfStudents_thenReturnsExpectedAverage() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(2);

        final Integer[] ages = new Integer[students.length];
        int baseAge = 17;
        int bias = 2;
        for (int i = 0; i < ages.length; i++) {
            ages[i] = baseAge++;
            bias += 2;
            baseAge += bias;
        }
        final Iterator<Integer> agesIterator = (Arrays.asList(ages)).iterator();

        Arrays.stream(students).forEach(student ->
                studentRepository.save(new Student(0L, student.getName(), agesIterator.next(), null)));

        final Double averageAgeExpected = Arrays.stream(ages).mapToInt(i -> i).average().orElse(0.0);

        ResponseEntity<Double> getAverageResponse = rest.getForEntity(
                studentUrl + "/stat/age/average",
                Double.class);

        Assertions.assertThat(getAverageResponse).isNotNull();
        Assertions.assertThat(getAverageResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getAverageResponse.getBody()).isEqualTo(averageAgeExpected);
    }

    @Test
    @DisplayName("Получение последних добавленных студентов -> список студентов получен")
    void whenGetLastStudents_thenReturnsExpectedListOfStudents() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);
        final int limit = 2;

        Arrays.stream(students).forEach(student ->
                studentRepository.save(new Student(0L, student.getName(), student.getAge(), null)));

        // Сначала получим список всех студентов, отсортируем его известным способом
        // и возьмём из полученного массива последние limit элементов.
        ResponseEntity<Student[]> getAllResponse = rest.getForEntity(studentUrl, Student[].class);

        Assertions.assertThat(getAllResponse).isNotNull();
        Assertions.assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getAllResponse.getBody()).isNotEmpty();
        Assertions.assertThat(getAllResponse.getBody()).hasSize(students.length);

        final Student[] sortedStudents = getAllResponse.getBody();
        Arrays.sort(sortedStudents, Comparator.comparingLong(Student::getId));
        final Student[] lastStudents = Arrays.copyOfRange(sortedStudents,
                students.length - limit, students.length);

        // Теперь получим список последних добавленных студентов
        // проверяемым способом.
        ResponseEntity<Student[]> getLastResponse = rest.getForEntity(
                studentUrl + "/stat/last/" + limit,
                Student[].class);

        Assertions.assertThat(getLastResponse).isNotNull();
        Assertions.assertThat(getLastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getLastResponse.getBody()).isNotEmpty();
        Assertions.assertThat(getLastResponse.getBody()).hasSize(limit);

        // Сравним полученные списки.
        final long[] expectedIds = Arrays.stream(lastStudents).mapToLong(Student::getId).toArray();
        final long[] actualIds = Arrays.stream(getLastResponse.getBody()).mapToLong(Student::getId).toArray();

        Assertions.assertThat(expectedIds).containsExactlyInAnyOrder(actualIds);
    }
}
