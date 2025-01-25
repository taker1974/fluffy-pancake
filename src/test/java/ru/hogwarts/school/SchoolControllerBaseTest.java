package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Objects;

public abstract class SchoolControllerBaseTest {

    protected final Student[] students = new Student[]{
            new Student(0, "John Doe", 18, null),
            new Student(0, "Jane Doe", 19, null),
            new Student(0, "John Smith", 20, null)
    };

    final Faculty[] faculties = new Faculty[]{
            new Faculty(100, "Faculty of Magic", "Fantastic Red", null),
            new Faculty(101, "Faculty of Science", "Deep Blue", null),
            new Faculty(102, "Faculty of Arts", "Smoky Brown", null)
    };

    public static Student createByAnother(Student student) {
        return new Student(0L, student.getName(), student.getAge(), null);
    }

    public static Faculty createByAnother(Faculty faculty) {
        return new Faculty(0L, faculty.getName(), faculty.getColor(), null);
    }

    public static Student copyFrom(Student student) {
        return new Student(student.getId(), student.getName(), student.getAge(), student.getFaculty());
    }

    public static Faculty copyFrom(Faculty faculty) {
        return new Faculty(faculty.getId(), faculty.getName(), faculty.getColor(), null);
    }

    public static Student setId(Student student, long id) {
        student.setId(id);
        return student;
    }

    public static Student setFaculty(Student student, Faculty faculty) {
        student.setFaculty(faculty);
        return student;
    }

    public static String buildJson(Student student) {
        try {
            return new JSONObject()
                    .put("id", student.getId())
                    .put("name", student.getName())
                    .put("age", student.getAge()).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    String buildJson(Faculty faculty) {
        try {
            return new JSONObject()
                    .put("id", faculty.getId())
                    .put("name", faculty.getName())
                    .put("color", faculty.getColor()).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public static final long BAD_ID = 45_334L;

    public static void assertResponse(ResponseEntity<Student> response, Student student) {

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isPositive();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(student.getName());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(student.getAge());
    }

    public static ResponseEntity<ErrorResponse> addSame(TestRestTemplate restTemplate,
                                                        String studentApiUrl, final Student student,
                                                        final long assignedId) {
        student.setId(assignedId);
        final var entity = new HttpEntity<>(student);
        return restTemplate.postForEntity(studentApiUrl, entity, ErrorResponse.class);
    }

    public static void assertErrorResponse(ResponseEntity<ErrorResponse> errorResponse,
                                           HttpStatus status, int code) {
        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(status);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code()).isEqualTo(code);
    }

    public static ResponseEntity<Faculty> addNew(TestRestTemplate restTemplate,
                                                 String facultyApiUrl, final Faculty faculty) {
        faculty.setId(0);
        final var entity = new HttpEntity<>(faculty);
        return restTemplate.postForEntity(facultyApiUrl, entity, Faculty.class);
    }

    public static ResponseEntity<ErrorResponse> addSame(TestRestTemplate restTemplate,
                                                        String facultyApiUrl, final Faculty faculty,
                                                        final long assignedId) {
        faculty.setId(assignedId);
        final var entity = new HttpEntity<>(faculty);
        return restTemplate.postForEntity(facultyApiUrl, entity, ErrorResponse.class);
    }

    public static ResponseEntity<Student> deleteStudent(TestRestTemplate restTemplate,
                                                        String studentApiUrl, final long assignedId) {
        return restTemplate.exchange(
                String.format("%s/%d", studentApiUrl, assignedId), HttpMethod.DELETE, null,
                Student.class);
    }

    public static ResponseEntity<Faculty> deleteFaculty(TestRestTemplate restTemplate,
                                                        String facultyApiUrl, final long assignedId) {
        return restTemplate.exchange(
                String.format("%s/%d", facultyApiUrl, assignedId), HttpMethod.DELETE, null,
                Faculty.class);
    }
}
