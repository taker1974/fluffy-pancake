package ru.hogwarts.school;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

/**
 * Из условий ДЗ<br>
 * <a href="https://my.sky.pro/student-cabinet/stream-lesson/145842/homework-requirements">SkyPro</a><br>
 * <a href="https://skyengpublic.notion.site/3-6-Spring-Boot-0070e5697e594bd0a5c6e5f96a29f950">Notion</a>:<br>
 * <p>
 * Задание 2.<br>
 * Шаг 1<br>
 * Создать класс для тестирования в пакете test.<br>
 * Создать по одному тесту на каждый эндпоинт контроллера StudentController, используя WebMvcTest.<br>
 * Критерии оценки:<br>
 * - в пакете test создан класс для тестирования StudentController;<br>
 * - для тестирования использовался WebMvcTest;<br>
 * - для каждого эндпоинта контроллера StudentController создан как минимум один тест.<br>
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {StudentController.class,
        StudentService.class, StudentRepository.class,
        Student.class, Faculty.class})
@WebMvcTest
class StudentControllerWebMvcTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    StudentRepository studentRepository;

    @MockitoSpyBean
    StudentService studentService;

    @InjectMocks
    StudentController studentController;

    final Student[] students;
    final int wrongId = 45334;

    StudentControllerWebMvcTest() {
        students = new Student[3];

        students[0] = new Student();
        students[1] = new Student();
        students[2] = new Student();

        students[0].setId(1);
        students[1].setId(2);
        students[2].setId(3);

        students[0].setName("John Doe");
        students[1].setName("Jane Doe");
        students[2].setName("Bob Jones");

        students[0].setAge(18);
        students[1].setAge(19);
        students[2].setAge(20);
    }

    String buildJson(Student student) {
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
    @DisplayName("Добавление студента: студент добавляется успешно")
    void whenAddStudent_thenReturnsExpectedStudent() throws Exception {

        final var student = students[0];
        final String studentJson = buildJson(students[0]);

        given(studentRepository.save(any(Student.class))).willReturn(student);

        // Добавим нового студента.
        given(studentRepository.findById(anyLong())).willReturn(Optional.empty());

        // Убедимся, что студент добавляется успешно.
        mvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));

        // Попытаемся добавить существующего студента.
        given(studentRepository.findById(anyLong())).willReturn(Optional.of(student));

        // Убедимся, что студент не добавляется.
        mvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentAlreadyExistsException.CODE));
    }

    @Test
    @DisplayName("Получение студента: студент возвращается успешно")
    void whenGetStudent_thenReturnsExpectedStudent() throws Exception {

        final var student = students[0];

        // Найдём студента.
        given(studentRepository.findById(student.getId())).willReturn(Optional.of(student));

        mvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));

        // Не найдём студента
        given(studentRepository.findById(student.getId())).willReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .get("/student/" + wrongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
    }

//    @Test
//    void whenUpdateStudent_thenReturnsUpdatedStudent() throws Exception {
//
//        final var student = students[0];
//        final var studentUpdated = new Student(student.getId(),
//                student.getName() + " updated",
//                student.getAge() + 2);
//        final String jsonUpdated = buildJson(studentUpdated);
//
//        // обновим существующего студента
//        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
//        when(studentRepository.save(any(Student.class))).thenReturn(studentUpdated);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/student")
//                        .content(jsonUpdated)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(studentUpdated.getId()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(studentUpdated.getName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(studentUpdated.getAge()));
//
//        // обновим несуществующего студента
//        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/student")
//                        .content(buildJson(studentUpdated))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
//    }
//
//    @Test
//    void whenDeleteStudent_thenReturnsDeletedStudent() throws Exception {
//
//        final var student = students[0];
//
//        // удалим существующего студента
//        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/student/" + student.getId())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
//
//        // удалим несуществующего студента
//        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/student/" + student.getId())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StudentNotFoundException.CODE));
//    }
//
//    @Test
//    void whenGetAllStudents_thenReturnsAllStudents() throws Exception {
//
//        // получим список всех студентов
//        when(studentRepository.findAll()).thenReturn(Arrays.asList(students));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(students.length));
//
//        // получим пустой список студентов
//        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/student")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    void whenFindStudentsByAge_thenReturnsStudentsOfAge() throws Exception {
//
//        // получим список всех студентов
//        final Student[] sameAgeStudents = new Student[]{students[0], students[1]};
//        when(studentRepository.findByAge(anyInt())).thenReturn(Arrays.asList(sameAgeStudents));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, "/student/filter/age/" + students[0].getAge())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(sameAgeStudents.length));
//
//        // получим пустой список студентов
//        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, "/student/filter/age/" + students[0].getAge())
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() throws Exception {
//
//        // получим список всех студентов
//        final Student[] ageInRangeStudents = new Student[]{students[0], students[1], students[2]};
//        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Arrays.asList(ageInRangeStudents));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(ageInRangeStudents.length));
//
//        // получим пустой список студентов
//        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    void whenGetFaculty_thenReturnsFaculty() throws Exception {
//
//        final var student = students[0];
//
//        var set = new HashSet<>(List.of(student));
////        when(faculty.getStudents(faculty.getId())).thenReturn(set);
//
//        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
//
//        // получим имя факультета
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, String.format("/student/%d/faculty", student.getId()))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()));
//
//        // не получим факультет потому, что он не установлен
//        student.setFaculty(null);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, String.format("/student/%d/faculty", student.getId()))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.name").doesNotExist());
//
//        // получим ошибку
//        when(studentService.getStudent(anyInt())).thenReturn(null);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .request(HttpMethod.GET, "/student/filter/age?fromAge=23&toAge=28") // без разницы
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
}
