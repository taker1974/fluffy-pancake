package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.ErrorResponse;
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
            new Faculty(0, "Faculty of Magic", "Fantastic Red", null),
            new Faculty(0, "Faculty of Science", "Deep Blue", null),
            new Faculty(0, "Faculty of Arts", "Smoky Brown", null)
    };

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

    public static void assertErrorResponse(ResponseEntity<ErrorResponse> errorResponse,
                                           HttpStatus status, int code) {
        Assertions.assertThat(errorResponse).isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(status);
        Assertions.assertThat(Objects.requireNonNull(errorResponse.getBody()).code()).isEqualTo(code);
    }
}
