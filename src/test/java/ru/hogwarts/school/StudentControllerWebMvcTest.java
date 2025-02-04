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
import static ru.hogwarts.school.tools.StringEx.replace;

@RequiredArgsConstructor
@ActiveProfiles("test-h2")
@ContextConfiguration(classes = {StudentController.class,
        CommonControllerAdvice.class, StudentControllerAdvice.class, FacultyControllerAdvice.class,
        StudentService.class, FacultyService.class,
        StudentRepository.class, FacultyRepository.class,
        Student.class, Faculty.class})
@WebMvcTest(controllers = StudentController.class)
class StudentControllerWebMvcTest extends SchoolControllerBaseTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    StudentRepository studentRepository;

    @MockitoBean
    FacultyRepository facultyRepository;

    @SuppressWarnings("unused")
    @MockitoSpyBean
    StudentService studentService;

    @SuppressWarnings("unused")
    @MockitoSpyBean
    FacultyService facultyService;

    @SuppressWarnings("unused")
    @InjectMocks
    StudentController studentController;

    @Test
    @DisplayName("Добавление студента -> возвращается id студента со статусом CREATED")
    void whenAddStudent_thenReturnsStudentId() throws Exception {

        final Student student = getInserted(students[0]);
        final String studentJson = buildJson(student);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty()); // студент не существует
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        mvc.perform(MockMvcRequestBuilders
                        .post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.matchesPattern("-?\\d+")));

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student)); // студент уже существует
        mvc.perform(MockMvcRequestBuilders
                        .post("/student/add")
                        .content(studentJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentAlreadyExistsException.class));
    }

    @Test
    @DisplayName("Получение студента -> студент получен со статусом OK")
    void whenGetStudent_thenReturnsStudent() throws Exception {

        final Student student = getInserted(students[0]);

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
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Обновление студента -> обновлённый студент получен со статусом OK")
    void whenUpdateStudent_thenReturnsUpdatedStudent() throws Exception {

        final Student student = getInserted(students[0]);
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
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Удаление студента -> возвращается статус NO_CONTENT")
    void whenDeleteStudent_thenReturnsStatusNoContent() throws Exception {

        final Student student = getInserted(students[0]);
        final long id = student.getId();

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        mvc.perform(MockMvcRequestBuilders
                        .delete(replace("/student/{id}/delete", id))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .delete(replace("/student/{id}/delete", id))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Установка факультета -> возвращается статус OK")
    void whenSetFaculty_thenReturnsStatusOk() throws Exception {

        final Student student = getInserted(students[0]);
        final long studentId = student.getId();

        final Faculty faculty = getInserted(faculties[0]);
        final long facultyId = faculty.getId();

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                replace("/student/{studentId}/faculty/{facultyId}", studentId, facultyId))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                replace("/student/{studentId}/faculty/{facultyId}", studentId, facultyId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(FacultyNotFoundException.class));
    }

    @Test
    @DisplayName("Получение факультета студента по id студента -> факультет получен")
    void whenGetFaculty_thenReturnsFaculty() throws Exception {

        final Faculty faculty = getInserted(faculties[0]);

        final Student student = getInserted(students[0]);
        final long studentId = student.getId();

        student.setFaculty(faculty);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mvc.perform(MockMvcRequestBuilders
                        .get(replace("/student/{id}/faculty", studentId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getFaculty().getName()));

        student.setFaculty(null);
        mvc.perform(MockMvcRequestBuilders
                        .get(replace("/student/{id}/faculty", studentId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.faculty.name").doesNotExist());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .get(replace("/student/{id}/faculty", BAD_ID))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Сброс факультета -> возвращается статус OK")
    void whenResetFaculty_thenReturnsStatusOk() throws Exception {

        final Student student = getInserted(students[0]);
        final long studentId = student.getId();
        final var set = new HashSet<>(List.of(student));

        final Faculty faculty = getInserted(faculties[0]);
        faculty.setStudents(set);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                replace("/student/{id}/faculty/reset", studentId))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        student.setFaculty(null);
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                replace("/student/{id}/faculty/reset", studentId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.faculty.name").doesNotExist());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders
                        .request(HttpMethod.PATCH,
                                replace("/student/{id}/faculty/reset", BAD_ID))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(StudentNotFoundException.class));
    }

    @Test
    @DisplayName("Получение всех студентов -> полный список студентов получен")
    void whenGetAllStudents_thenReturnsAllStudents() throws Exception {

        long baseId = 10;
        for (Student value : students) {
            value.setId(baseId++);
            value.setFaculty(null);
        }

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
                        .get("/student/filter/age/10") // без разницы
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(sameAgeStudents.length));

        when(studentRepository.findByAge(anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/filter/age/10") // без разницы
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

    @Test
    void whenGetCountOfStudents_thenReturnsCountOfStudents() throws Exception {

        when(studentRepository.getCountOfStudents()).thenReturn((long) students.length);
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/stat/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.matchesPattern("\\d+")));
    }

    @Test
    void whenGetAverageAgeOfStudents_thenReturnsDouble() throws Exception {

        final Double averageAge = Arrays.stream(students).mapToInt(Student::getAge).average().orElse(0);

        when(studentRepository.getAverageAgeOfStudents()).thenReturn(averageAge);
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/stat/age/average")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.matchesPattern("\\d+(\\.\\d+|,\\d+)?")))
                .andExpect(MockMvcResultMatchers.content()
                        .string(averageAge.toString()));
    }

    @Test
    void whenGetLastAddedStudents_thenReturnsExpectedStudents() throws Exception {

        final List<Student> lastStudents = Arrays.asList(students);
        when(studentRepository.getLastStudentsById(anyInt())).thenReturn(lastStudents);
        mvc.perform(MockMvcRequestBuilders
                        .get("/student/stat/last/3") // без разницы, какой здесь лимит
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(students.length));
    }
}
