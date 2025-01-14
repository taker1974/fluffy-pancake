// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
@WebMvcTest({StudentController.class, StudentRepository.class, StudentService.class})
@ContextConfiguration(classes = {SchoolApplication.class, StudentController.class, StudentRepository.class, StudentService.class})
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoSpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void whenAddStudent_thenReturnExpectedStudent() throws Exception {

        final var student = new Student(63, "Chris Muñoz", 19);

        // json для POST
        var studentObject = new JSONObject();
        studentObject.put("name", student.getName());
        studentObject.put("age", student.getAge());

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(student.getAge()));
    }
}

