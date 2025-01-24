package ru.hogwarts.school;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

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

    protected final long wrongId = 45334L;

    public static ResponseEntity<Student> addNew(TestRestTemplate restTemplate,
                                                 String studentApiUrl, final Student student) {
        student.setId(0);
        final var entity = new HttpEntity<>(student);
        return restTemplate.postForEntity(
                studentApiUrl, entity, Student.class);
    }

    public static ResponseEntity<ErrorResponse> addSame(TestRestTemplate restTemplate,
                                                        String studentApiUrl, final Student student,
                                                        final long assignedId) {
        student.setId(assignedId);
        final var entity = new HttpEntity<>(student);
        return restTemplate.postForEntity(
                studentApiUrl, entity, ErrorResponse.class);
    }

    public static ResponseEntity<Faculty> addNew(TestRestTemplate restTemplate,
                                                 String facultyApiUrl, final Faculty faculty) {
        faculty.setId(0);
        final var entity = new HttpEntity<>(faculty);
        return restTemplate.postForEntity(
                facultyApiUrl, entity, Faculty.class);
    }

    public static ResponseEntity<ErrorResponse> addSame(TestRestTemplate restTemplate,
                                                        String facultyApiUrl, final Faculty faculty,
                                                        final long assignedId) {
        faculty.setId(assignedId);
        final var entity = new HttpEntity<>(faculty);
        return restTemplate.postForEntity(
                facultyApiUrl, entity, ErrorResponse.class);
    }
}
