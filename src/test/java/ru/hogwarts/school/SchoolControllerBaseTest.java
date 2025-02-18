package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Objects;
import java.util.Random;

public abstract class SchoolControllerBaseTest {

    protected final Student[] students = new Student[]{
            new Student(0, "John Doe", 18, null),
            new Student(0, "Jane Doe", 19, null),
            new Student(0, "John Smith", 23, null),
            new Student(0, "Amy Winehouse", 27, null),
            new Student(0, "Даниил Хармс", 37, null)
    };

    final Faculty[] faculties = new Faculty[]{
            new Faculty(0, "Faculty of Magic", "Fantastic Red", null),
            new Faculty(0, "Faculty of Science", "Deep Blue", null),
            new Faculty(0, "Faculty of Arts", "Smoky Brown", null)
    };

    public static Student getNew(final Student student) {
        return new Student(
                0L,
                student.getName(),
                student.getAge(),
                null);
    }

    public static Student getInserted(final Student student) {
        var random = new Random();
        return new Student(
                random.nextLong(),
                student.getName(),
                student.getAge(),
                null);
    }

    public Faculty getNew(final Faculty faculty) {
        return new Faculty(
                0L,
                faculty.getName(),
                faculty.getColor(),
                null);
    }

    public Faculty getInserted(final Faculty faculty) {
        var random = new Random();
        return new Faculty(
                random.nextLong(),
                faculty.getName(),
                faculty.getColor(),
                null);
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

    public static void assertResponse(ResponseEntity<Faculty> response, Faculty faculty) {

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isPositive();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(faculty.getName());
        Assertions.assertThat(response.getBody().getColor()).isEqualTo(faculty.getColor());
    }

    public static void assertErrorResponse(ResponseEntity<ErrorResponseDto> errorResponse,
                                           HttpStatus status, int code) {
        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(status);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code()).isEqualTo(code);
    }
}
