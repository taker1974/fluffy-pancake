// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * StudentControllerWebMvcTest.<br>
 * <p>
 * Из условий ДЗ<br>
 * <a href="https://my.sky.pro/student-cabinet/stream-lesson/145842/homework-requirements">SkyPro</a><br>
 * <a href="https://skyengpublic.notion.site/3-6-Spring-Boot-0070e5697e594bd0a5c6e5f96a29f950">Notion</a>:<br>
 * Задание 2.<br>
 * Шаг 1<br>
 * Создать класс для тестирования в пакете test.<br>
 * Создать по одному тесту на каждый эндпоинт контроллера StudentController, используя WebMvcTest.<br>
 * Критерии оценки:<br>
 * - в пакете test создан класс для тестирования StudentController;<br>
 * - для тестирования использовался WebMvcTest;<br>
 * - для каждого эндпоинта контроллера StudentController создан как минимум один тест.<br>
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SchoolApplication.class)
@AutoConfigureMockMvc
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private final Student[] students = {
            new Student(1, "John Smith", 18),
            new Student(2, "Jane Doe", 19),
            new Student(3, "Bob Jones", 20),
            new Student(4, "Alice Brown", 21),
            new Student(5, "David Miller", 22),
            new Student(6, "Mary Wilson", 23),
            new Student(7, "James Taylor", 24),
            new Student(8, "Jennifer Lee", 25),
            new Student(9, "William Davis", 26),
    };

    private final int wrongId = 45334;

    @Spy
    private Faculty faculty = new Faculty(1, "Faculty", "0x4f");

    private String buildJson(Student student) {
        try {
            return new JSONObject()
                    .put("id", student.getId())
                    .put("name", student.getName())
                    .put("age", student.getAge()).toString();
        } catch (JSONException e) {
            return "";
        }
    }

    @Test
    void whenAddStudent_thenReturnsExpectedStudent() throws Exception {

        final var student = students[0];
        final String studentJson = buildJson(students[0]);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        // убедимся, что студент добавляется успешно
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
    }

    @Test
    void whenGetStudent_thenReturnsExpectedStudent() throws Exception {

        final var student = students[0];

        // найдём студента
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));

        // не найдём студента
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + wrongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
    }

    @Test
    void whenUpdateStudent_thenReturnsUpdatedStudent() throws Exception {

        final var student = students[0];
        final var studentUpdated = new Student(student.getId(),
                student.getName() + " updated",
                student.getAge() + 2);
        final String jsonUpdated = buildJson(studentUpdated);

        // обновим существующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(studentUpdated);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(jsonUpdated)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(studentUpdated.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(studentUpdated.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(studentUpdated.getAge()));

        // обновим несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(buildJson(studentUpdated))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
    }

    @Test
    void whenDeleteStudent_thenReturnsDeletedStudent() throws Exception {

        final var student = students[0];

        // удалим существующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));

        // удалим несуществующего студента
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
    }

    @Test
    void whenGetAllStudents_thenReturnsAllStudents() throws Exception {

        // получим список всех студентов
        when(studentRepository.findAll()).thenReturn(Arrays.asList(students));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(students.length));

        // получим пустой список студентов
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void whenFindStudentsByAge_thenReturnsStudentsOfAge() throws Exception {

        // получим список всех студентов
        final Student[] sameAgeStudents = new Student[]{students[0], students[1]};
        when(studentRepository.findByAge(anyInt())).thenReturn(Arrays.asList(sameAgeStudents));

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/student/filter/age/" + students[0].getAge())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(sameAgeStudents.length));

        // получим пустой список студентов
        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/student/filter/age/" + students[0].getAge())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() throws Exception {

        // получим список всех студентов
        final Student[] ageInRangeStudents = new Student[]{students[0], students[1], students[2]};
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Arrays.asList(ageInRangeStudents));

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(ageInRangeStudents.length));

        // получим пустой список студентов
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void whenGetFaculty_thenReturnsFaculty() throws Exception {

        final var student = students[0];

        var set = new HashSet<>(List.of(student));
//        when(faculty.getStudents(faculty.getId())).thenReturn(set);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        // получим имя факультета
        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, String.format("/student/%d/faculty", student.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()));

        // не получим факультет потому, что он не установлен
        student.setFaculty(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, String.format("/student/%d/faculty", student.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist());

        // получим ошибку
        when(studentService.getStudent(anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
