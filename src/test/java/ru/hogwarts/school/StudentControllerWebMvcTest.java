package ru.hogwarts.school;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.controller.advice.CommonControllerAdvice;
import ru.hogwarts.school.controller.advice.FacultyControllerAdvice;
import ru.hogwarts.school.controller.advice.StudentControllerAdvice;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/* https://habr.com/ru/companies/otus/articles/746414/

Аннотацию WebMvcTest можно использовать для теста Spring MVC, ориентированного только на компоненты Spring MVC.
Использование этой аннотации отключит полную автоконфигурацию и вместо этого применит только конфигурацию,
относящуюся к тестам MVC (например, @Controller, @ControllerAdvice, @JsonComponent, Converter/GenericConverter,
Filter, WebMvcConfigurer и HandlerMethodArgumentResolver bean-компоненты, но не @Component, @Service или @Repository
бобы).
 */

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
@RequiredArgsConstructor
@ActiveProfiles("test-h2")
@ContextConfiguration(classes = {StudentController.class,
        CommonControllerAdvice.class, StudentControllerAdvice.class, FacultyControllerAdvice.class,
        StudentService.class, FacultyService.class,
        StudentRepository.class, FacultyRepository.class,
        Student.class, Faculty.class})
@WebMvcTest
class StudentControllerWebMvcTest extends SchoolControllerBaseTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    StudentRepository studentRepository;

    @MockitoBean
    FacultyRepository facultyRepository;

    @MockitoSpyBean
    StudentService studentService;

    @MockitoSpyBean
    FacultyService facultyService;

    @InjectMocks
    StudentController studentController;

    @Test
    @DisplayName("Добавление студента -> возвращается id студента со статусом CREATED")
    void whenAddStudent_thenReturnsStudentId() throws Exception {

        final Student student1 = students[0];
        final String studentJson = buildJson(students[0]);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty()); // студент не существует
        when(studentRepository.save(any(Student.class))).thenReturn(student1);
        mvc.perform(MockMvcRequestBuilders
                        .post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.matchesPattern("\\d+")));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student1)); // студент уже существует
        mvc.perform(MockMvcRequestBuilders
                        .post("/student/add")
                        .content(studentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentAlreadyExistsException.class));
    }

    @Test
    @DisplayName("Получение студента -> студент получен со статусом OK")
    void whenGetStudent_thenReturnsStudent() throws Exception {

        final Student student = students[0];

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student)); // студент есть
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty()); // студент не существует
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/" + BAD_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен со статусом OK")
    void whenUpdateStudent_thenReturnsUpdatedStudent() throws Exception {

        final Student student = students[0];
        final Student studentUpdated = new Student(
                student.getId(),
                student.getName() + " updated",
                student.getAge() + 2,
                null);
        final String jsonUpdated = buildJson(studentUpdated);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(studentUpdated);
        mvc.perform(MockMvcRequestBuilders
                        .put("/student/update")
                        .content(jsonUpdated)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(studentUpdated.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(studentUpdated.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(studentUpdated.getAge()));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .put("/student/update")
                        .content(buildJson(studentUpdated))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Удаление студента -> возвращается статус NO_CONTENT")
    void whenDeleteStudent_thenReturnsStatusNoContent() throws Exception {

        final Student student = students[0];

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        mvc.perform(MockMvcRequestBuilders
                        .delete("/student/delete/" + student.getId())
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .delete("/student/delete/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Установка факультета -> возвращается статус OK")
    void whenSetFaculty_thenReturnsStatusOk() throws Exception {

        final Student student = students[0];
        student.setFaculty(null);
        final Faculty faculty = new Faculty(900, "Факультет 1", "Вечно синие", null);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                String.format("/student/%d/faculty/%d", student.getId(), faculty.getId()))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                String.format("/student/%d/faculty/%d", student.getId(), faculty.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class));
    }

    @Test
    @DisplayName("Получение факультета студента по id студента -> факультет получен")
    void whenGetFaculty_thenReturnsFaculty() throws Exception {

        final Student student = students[0];
        final var set = new HashSet<>(List.of(student));
        student.setFaculty(new Faculty(1, "Факультет 1", "Вечно синие", set));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/student/%d/faculty", student.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getFaculty().getName()));

        student.setFaculty(null);
        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/student/%d/faculty", student.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.faculty.name").doesNotExist());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/student/%d/faculty", BAD_ID))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Сброс факультета -> возвращается статус OK")
    void whenResetFaculty_thenReturnsStatusOk() throws Exception {

        final Student student = students[0];
        final var set = new HashSet<>(List.of(student));

        student.setFaculty(new Faculty(1, "Факультет 1", "Вечно синие", set));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH, String.format("/student/%d/faculty/reset", student.getId()))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        student.setFaculty(null);
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH, String.format("/student/%d/faculty/reset", student.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.faculty.name").doesNotExist());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH, String.format("/student/%d/faculty/reset", BAD_ID))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() throws Exception {

        when(studentRepository.findAll()).thenReturn(Arrays.asList(students));
        mvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(students.length));

        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Поиск студентов по точному возрасту -> список студентов получен")
    void whenFindStudentsByAge_thenReturnsStudentsOfAge() throws Exception {

        final Student[] sameAgeStudents = new Student[]{students[0], students[1]};

        when(studentRepository.findByAge(anyInt())).thenReturn(Arrays.asList(sameAgeStudents));
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/age/" + students[0].getAge())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(sameAgeStudents.length));

        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/age/" + students[0].getAge())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Поиск студентов по диапазону возраста -> список студентов получен")
    void whenFindStudentsByAgeBetween_thenReturnsStudentsOfAgeRange() throws Exception {

        final Student[] ageInRangeStudents = new Student[]{students[1], students[2]};

        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Arrays.asList(ageInRangeStudents));
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/age/between?fromAge=23&toAge=28") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(ageInRangeStudents.length));

        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/age/between?fromAge=23&toAge=28") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }
}
