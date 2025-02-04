package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.avatar.AvatarNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static ru.hogwarts.school.tools.StringEx.replace;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-pg")
class AvatarControllerIntegrityTest extends SchoolControllerBaseTest {

    public static final String IMAGE_PARAM_KEY = "file";

    public static final String IMAGE_PATH_ORIGINAL = "images";
    public static final String IMAGE_NAME_ORIGINAL = "test-upload-200x30.jpg";
    public static final String IMAGE_PATH = IMAGE_PATH_ORIGINAL + "/" + IMAGE_NAME_ORIGINAL;

    @SuppressWarnings("unused")
    @Value("${avatars.path}")
    private String avatarsPath;

    private final AvatarController avatarController;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    private final TestRestTemplate rest;

    private final String avatarUrl;

    AvatarControllerIntegrityTest(@LocalServerPort int port,
                                  @Autowired AvatarController avatarController,
                                  @Autowired AvatarRepository avatarRepository,
                                  @Autowired StudentRepository studentRepository,
                                  @Autowired TestRestTemplate restTemplate) {

        avatarUrl = "http://localhost:" + port + "/school/avatar";

        this.avatarController = avatarController;
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;

        this.rest = restTemplate;
    }

    void deleteAllFiles() {
        File directory = new File(avatarsPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        Assertions.assertThat(file.delete()).isTrue();
                    }
                }
            }
        }
    }

    @AfterEach
    void tearDown() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
        deleteAllFiles();
    }

    @Test
    @DisplayName("Валидация контекста")
    void contextLoads() {
        Assertions.assertThat(avatarUrl).isNotBlank();
        Assertions.assertThat(avatarController).isNotNull();
        Assertions.assertThat(avatarRepository).isNotNull();
        Assertions.assertThat(studentRepository).isNotNull();
        Assertions.assertThat(rest).isNotNull();
    }

    static byte[] getImageBytes() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final URI uri = Objects.requireNonNull(classloader.getResource(IMAGE_PATH)).toURI();
            return Files.readAllBytes(Path.of(uri));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getImagePath() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final URI uri = Objects.requireNonNull(classloader.getResource(IMAGE_PATH)).toURI();
            return uri.getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    long uploadAvatar(final String fileName, final Student student) {

        var resource = new ByteArrayResource(getImageBytes()) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(IMAGE_PARAM_KEY, resource);

        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Long> responseEntity = rest.postForEntity(
                replace("{avatarUrl}/student/{studentId}/upload", avatarUrl, student.getId()),
                requestEntity, Long.class);

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(student.getId());

        return responseEntity.getBody();
    }

    @Test
    @DisplayName("Загрузка аватара студента -> возвращается id аватара")
    void whenUploadAvatar_thenReturnsAvatarId() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(avatarRepository.findByStudentId(student.getId())).isPresent());
    }

    @Test
    @DisplayName("Удаление аватара студента -> аватар удаляется")
    void whenDeleteAvatar_thenAvatarDisappears() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        final ResponseEntity<Void> responseEntity = rest.exchange(
                replace("{avatarUrl}/student/{studentId}/delete", avatarUrl, studentsLoaded[1].getId()),
                HttpMethod.DELETE, null, Void.class);

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        final ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                replace("{avatarUrl}/student/{studentId}/delete", avatarUrl, studentsLoaded[1].getId()),
                HttpMethod.DELETE, null, ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, AvatarNotFoundException.CODE);
    }

    @Test
    @DisplayName("Загрузка аватара студента из БД -> возвращается аватар")
    void whenDownloadAvatarFromDb_thenAvatarLoaded() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        final ResponseEntity<byte[]> responseEntity = rest.exchange(
                replace("{avatarUrl}/student/db/{studentId}", avatarUrl, studentsLoaded[1].getId()),
                HttpMethod.GET, null, byte[].class);

        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();

        final byte[] image = responseEntity.getBody();
        final byte[] imageOrig = getImageBytes();

        Assertions.assertThat(image).isNotNull();
        Assertions.assertThat(imageOrig).isNotNull();
        Assertions.assertThat(image).isEqualTo(imageOrig);

        final ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                replace("{avatarUrl}/student/db/{studentId}", avatarUrl, BAD_ID),
                HttpMethod.GET, null, ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, AvatarNotFoundException.CODE);
    }

    @Test
    @DisplayName("Загрузка аватара студента из файла -> возвращается аватар")
    void whenDownloadAvatarFromFile_thenAvatarLoaded() {

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        ResponseEntity<Resource> response = rest.exchange(
                replace("{avatarUrl}/student/file/{studentId}", avatarUrl, studentsLoaded[1].getId()),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Resource.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();

        byte[] image = null;
        try {
            image = response.getBody().getContentAsByteArray();
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        final byte[] imageOrig = getImageBytes();

        Assertions.assertThat(image).isNotNull();
        Assertions.assertThat(imageOrig).isNotNull();
        Assertions.assertThat(image).isEqualTo(imageOrig);

        ResponseEntity<ErrorResponseDto> errorResponse = rest.exchange(
                replace("{avatarUrl}/student/file/{studentId}", avatarUrl, BAD_ID),
                HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), ErrorResponseDto.class);

        assertErrorResponse(errorResponse, HttpStatus.NOT_FOUND, AvatarNotFoundException.CODE);
    }

    @Test
    @DisplayName("Получение списка всех аватарок -> возвращается список аватарок")
    void whenGetAllAvatars_thenReturnsListOfAvatars() {

        ResponseEntity<Avatar[]> emptyResponse = rest.getForEntity(avatarUrl, Avatar[].class);

        Assertions.assertThat(emptyResponse).isNotNull();
        Assertions.assertThat(emptyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyResponse.getBody()).isNotNull();
        Assertions.assertThat(emptyResponse.getBody()).isEmpty();

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        ResponseEntity<Avatar[]> response = rest.getForEntity(avatarUrl, Avatar[].class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isNotEmpty();
        Assertions.assertThat(response.getBody()).hasSize(students.length);
    }

    @Test
    @DisplayName("Получение списка всех аватарок постранично -> возвращается страница аватарок")
    void whenGetAllAvatarsPaginated_thenReturnsPageWithAvatars() {

        // Здесь самая примитивная реализация тестирования Page<Avatar>.
        // Пока не разобрался, как работать с Page<T> на уровне запросов.

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(5);

        final int page = 1;
        final int size = 2;
        final String pageUrl = replace("{avatarUrl}/pages?page={page}&size={size}", avatarUrl, page, size);

        ResponseEntity<Object> emptyResponse = rest.getForEntity(pageUrl, Object.class);

        Assertions.assertThat(emptyResponse).isNotNull();
        Assertions.assertThat(emptyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(emptyResponse.getBody()).isNotNull();

        Assertions.assertThat(students).hasSizeGreaterThanOrEqualTo(3);

        Arrays.stream(students).forEach(student -> studentRepository.save(getNew(student)));
        final Student[] studentsLoaded = studentRepository.findAll().toArray(Student[]::new);

        Arrays.stream(studentsLoaded).forEach(student ->
                Assertions.assertThat(uploadAvatar(getImagePath(), student)).isEqualTo(student.getId()));

        ResponseEntity<Object> response = rest.getForEntity(pageUrl, Object.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody()).isInstanceOf(Page.class);
    }
}
