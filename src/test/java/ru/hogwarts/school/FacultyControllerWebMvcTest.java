package ru.hogwarts.school;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.advice.CommonControllerAdvice;
import ru.hogwarts.school.controller.advice.FacultyControllerAdvice;
import ru.hogwarts.school.controller.advice.StudentControllerAdvice;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ActiveProfiles("test-h2")
@ContextConfiguration(classes = {FacultyController.class,
        CommonControllerAdvice.class, FacultyControllerAdvice.class, StudentControllerAdvice.class,
        FacultyService.class, StudentService.class,
        FacultyRepository.class, StudentRepository.class,
        Faculty.class, Student.class})
@WebMvcTest
class FacultyControllerWebMvcTest extends SchoolControllerBaseTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    FacultyRepository facultyRepository;

    @MockitoBean
    StudentRepository studentRepository;

    @MockitoSpyBean
    FacultyService facultyService;

    @MockitoSpyBean
    StudentService studentService;

    @InjectMocks
    FacultyController studentController;

    @Test
    @DisplayName("Добавление факультета -> факультет добавлен")
    void whenAddFaculty_thenReturnsExpectedFaculty() throws Exception {

        final Faculty faculty = faculties[0];
        final String facultyJson = buildJson(faculties[0]);

        // Убедимся, что новый факультет добавляется успешно.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty()); // факультет не существует
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(faculty.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));

        // Попытаемся добавить существующий факультет и
        // убедимся, что факультет не добавляется.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty)); // факультет уже существует

        mvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyAlreadyExistsException.class));
    }

    @Test
    @DisplayName("Получение факультета -> факультет получен")
    void whenGetFaculty_thenReturnsExpectedFaculty() throws Exception {

        final Faculty faculty = faculties[0];

        // Найдём факультет.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty)); // факультет существует

        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(faculty.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));

        // Не найдём факультет.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + wrongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class));
    }

    @Test
    @DisplayName("Обновление факультета -> обновлённый факультет получен")
    void whenUpdateFaculty_thenReturnsUpdatedFaculty() throws Exception {

        final Faculty faculty = faculties[0];
        final Faculty facultyUpdated = new Faculty(
                faculty.getId(),
                faculty.getName() + " Great Again",
                faculty.getColor() + " Plus Shining White",
                null);
        final String jsonUpdated = buildJson(facultyUpdated);

        // Обновим существующего факультета.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(facultyUpdated);

        mvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(jsonUpdated)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(facultyUpdated.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(facultyUpdated.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(facultyUpdated.getColor()));

        // Обновим несуществующий факультет.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(buildJson(facultyUpdated))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class));
    }

    @Test
    @DisplayName("Удаление факультета -> удалённый факультет получен в последний раз")
    void whenDeleteFaculty_thenReturnsDeletedFaculty() throws Exception {

        final Faculty faculty = faculties[0];

        // Удалим существующий факультет.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(faculty.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(faculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(faculty.getColor()));

        // Удалим несуществующий факультет.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/" + faculty.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class));
    }

    @Test
    @DisplayName("Поиск факультетов по \"цвету\" -> список факультетов получен")
    void whenFindFacultiesByColor_thenReturnsFacultiesOfColor() throws Exception {

        // Получим список всех факультетов искомого цвета.
        final Faculty[] sameColorFaculties = new Faculty[]{faculties[0], faculties[1]};
        when(facultyRepository.findByColorIgnoreCase(anyString())).thenReturn(Arrays.asList(sameColorFaculties));

        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter/color/" + faculties[0].getColor()) // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(sameColorFaculties.length));

        // Получим пустой список факультетов.
        when(facultyRepository.findByColorIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter/color/" + faculties[0].getColor())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Поиск факультетов по названию или \"цвету\" -> список факультетов получен")
    void whenFindFacultiesByNameOrColor_thenReturnsFaculties() throws Exception {

        // Получим список всех факультетов искомых названия и цвета.
        final Faculty[] wantedFaculties = new Faculty[]{faculties[0], faculties[2]};
        when(facultyRepository.findByNameOrColorIgnoreCase(anyString(), anyString()))
                .thenReturn(Arrays.asList(wantedFaculties));

        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/faculty/filter?name=%s&color=%s", faculties[0].getName(),
                                faculties[0].getColor()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(wantedFaculties.length));

        // Получим пустой список факультетов.
        when(facultyRepository.findByNameOrColorIgnoreCase(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/faculty/filter?name=%s&color=%s", faculties[0].getName(),
                                faculties[0].getColor()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Получение всех факультетов -> полный список факультетов получен")
    void whenGetAllFaculties_thenReturnsAllFaculties() throws Exception {

        // Получим объекты всех факультетов.
        when(facultyRepository.findAll()).thenReturn(Arrays.asList(faculties));

        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(faculties.length));

        // Получим пустой список факультетов.
        when(facultyRepository.findAll()).thenReturn(Collections.emptyList());

        mvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    final Student[] students = new Student[]{
            new Student(700, "John Doe", 18, null),
            new Student(701, "Jane Doe", 19, null),
            new Student(702, "John Smith", 20, null)
    };

    @Test
    @DisplayName("Получение всех студентов факультета -> полный список студентов получен")
    void whenGetStudentsOfFaculty_thenReturnsStudents() throws Exception {

        final Faculty faculty = faculties[0];
        final HashSet<Student> members = new HashSet<>(Arrays.asList(students));

        for (Student member : members) {
            member.setFaculty(faculty);
        }
        faculty.setStudents(members);

        // Получим нужный факультет со списком студентов.
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/faculty/%d/students", faculty.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(faculties.length));

        // Получим пустой список студентов.
        faculty.getStudents().clear();

        mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/faculty/%d/students", faculty.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }
}
