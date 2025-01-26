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

    // Какую роль здесь играет @Autowired? (Без этого не работает.)

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
    @DisplayName("Добавление факультета -> факультет добавлен")
    void whenAddFaculty_thenReturnsExpectedFaculty() {

        // Мне удобно, когда в каждом методе перед глазами
        // есть подстрока адреса запроса - так легче не запутаться.
        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем новый факультет.
        ResponseEntity<Faculty> addResponse = rest.postForEntity(url, new HttpEntity<>(faculty), Faculty.class);

        assertResponse(addResponse, faculty);

        // Пытаемся добавить его ещё раз.
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url, HttpMethod.POST,
                new HttpEntity<>(Objects.requireNonNull(addResponse.getBody())), ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.BAD_REQUEST, FacultyAlreadyExistsException.CODE);
    }

    @Test
    @DisplayName("Получение факультета -> факультет получен")
    void whenGetFaculty_thenReturnsExpectedFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем и получаем новый факультет.
        ResponseEntity<Faculty> addResponse = rest.postForEntity(url, new HttpEntity<>(faculty), Faculty.class);
        final long id = Objects.requireNonNull(addResponse.getBody()).getId();

        ResponseEntity<Faculty> getResponse = rest.getForEntity(url + "/" + id, Faculty.class);
        assertResponse(getResponse, faculty);

        // Пробуем читать несуществующий факультет.
        ResponseEntity<ErrorResponse> errorResponse = rest.getForEntity(url + "/" + BAD_ID, ErrorResponse.class);
        assertErrorResponse(errorResponse, HttpStatus.BAD_REQUEST, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Обновление факультета -> обновлённый факультет получен")
    void whenUpdateFaculty_thenReturnsUpdatedFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем новый факультет.
        ResponseEntity<Faculty> addResponse = rest.postForEntity(url, new HttpEntity<>(faculty), Faculty.class);
        Faculty addedFaculty = Objects.requireNonNull(addResponse.getBody());

        // Обновим того же студента.
        final Faculty updatedFaculty = copyFrom(addedFaculty);
        updatedFaculty.setName(faculty.getName() + " +50% for free!");
        updatedFaculty.setColor(faculty.getColor() + " and Mostly Red!");

        rest.exchange(url, HttpMethod.PUT, new HttpEntity<>(updatedFaculty), Faculty.class);

        // Прочитаем его и проверим.
        ResponseEntity<Faculty> getResponse = rest.getForEntity(url + "/" + addedFaculty.getId(), Faculty.class);

        assertResponse(getResponse, updatedFaculty);

        // Пытаемся обновить несуществующего студента.
        updatedFaculty.setId(BAD_ID);
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url, HttpMethod.PUT,
                new HttpEntity<>(updatedFaculty), ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.BAD_REQUEST, FacultyNotFoundException.CODE);
    }

    @Test
    @DisplayName("Удаление факультета -> удалённый факультет получен в последний раз")
    void whenDeleteFaculty_thenReturnsDeletedFaculty() {

        final String url = baseUrl + "/school/faculty";
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавляем новый факультет.
        ResponseEntity<Faculty> addResponse = rest.postForEntity(url, new HttpEntity<>(faculty), Faculty.class);
        Faculty addedFaculty = Objects.requireNonNull(addResponse.getBody());

        // Удаляем его.
        ResponseEntity<Faculty> deleteResponse = rest.exchange(url + "/" + addedFaculty.getId(),
                HttpMethod.DELETE, null, Faculty.class);

        assertResponse(deleteResponse, addedFaculty);

        // Пытаемся удалить несуществующий факультет.
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange(url + "/" + addedFaculty.getId(),
                HttpMethod.DELETE, null, ErrorResponse.class);

        assertErrorResponse(errorResponse, HttpStatus.BAD_REQUEST, FacultyNotFoundException.CODE);
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

        // Добавим факультеты из массива.
        Arrays.stream(faculties).forEach(faculty -> {
            faculty.setColor(colorsIterator.next());
            rest.postForEntity(url, new HttpEntity<>(createByAnother(faculty)), Faculty.class);
        });

        // Найдём факультет по цвету. Он должен быть в один.
        final ResponseEntity<Faculty[]> arrayResponse = rest.getForEntity(url + "/filter/color?color=" + colors[0],
                Faculty[].class);

        Assertions.assertThat(arrayResponse).isNotNull();
        Assertions.assertThat(arrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayResponse.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("Получение всех факультетов -> полный список факультетов получен")
    void whenGetAllFaculties_thenReturnsAllFaculties() {

        final String url = baseUrl + "/school/faculty";

        // Попробуем прочитать все факультеты из пустого хранилища.
        final ResponseEntity<Faculty[]> emptyArrayResponse = rest.getForEntity(url, Faculty[].class);

        Assertions.assertThat(emptyArrayResponse).isNotNull();
        Assertions.assertThat(emptyArrayResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyArrayResponse.getBody()).isEmpty();

        // Добавим факультеты из массива.
        Arrays.stream(faculties).forEach(faculty ->
                rest.postForEntity(url, new HttpEntity<>(createByAnother(faculty)), Faculty.class));

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
        final Faculty faculty = createByAnother(faculties[0]);

        // Добавим один факультет без студентов и прочитаем список студентов.
        rest.postForEntity(url, new HttpEntity<>(createByAnother(faculty)), Faculty.class);

        ResponseEntity<Faculty[]> arrayOfSingleResponse = rest.getForEntity(url, Faculty[].class);
        Assertions.assertThat(arrayOfSingleResponse).isNotNull();
        Assertions.assertThat(arrayOfSingleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayOfSingleResponse.getBody()).isNotEmpty();
        Assertions.assertThat(arrayOfSingleResponse.getBody()).hasSize(1);
        Assertions.assertThat(arrayOfSingleResponse.getBody()[0].getStudents()).isNull();

        // Добавим студентов в хранилище, устанавливая каждому уже добавленный факультет.
        Arrays.stream(students).forEach(student -> {
            Student studentWithFaculty = createByAnother(student);
            studentWithFaculty.setFaculty(faculty);
            rest.postForEntity(baseUrl + "/school/student", new HttpEntity<>(student), Student.class);
        });

        // Добавим ещё пару факультетов без студентов.
        rest.postForEntity(url, new HttpEntity<>(createByAnother(faculties[1])), Faculty.class);
        rest.postForEntity(url, new HttpEntity<>(createByAnother(faculties[2])), Faculty.class);

        // Получим все факультеты и найдём факультет с непустым списком студентов.
        final ResponseEntity<Faculty[]> arrayOfFaculties = rest.getForEntity(url, Faculty[].class);

        Assertions.assertThat(arrayOfFaculties).isNotNull();
        Assertions.assertThat(arrayOfFaculties.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(arrayOfFaculties.getBody()).isNotEmpty();
        Assertions.assertThat(arrayOfFaculties.getBody()).hasSize(3);

        Arrays.stream(arrayOfFaculties.getBody())
                .filter(Objects::nonNull)
                .filter(x -> x.getStudents() != null)
                .forEach(x ->
                        Assertions.assertThat(x.getStudents()).hasSize(students.length));
    }
}
