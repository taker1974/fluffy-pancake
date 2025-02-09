package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping(value = "/student")
@Tag(name = "Студенты")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового студента. Возвращает id нового студента")
    @PostMapping("/add")
    public Long addStudent(@RequestBody Student student) {
        return studentService.addStudent(student).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение студента по id")
    @GetMapping(value = "/{id}")
    public Student getStudent(@PathVariable long id) {
        return studentService.getStudent(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление существующего студента")
    @PutMapping("/update")
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление существующего студента по id")
    @DeleteMapping(value = "/{id}/delete")
    public void deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Установка факультета студента по id студента и id факультета")
    @PatchMapping(value = "/{studentId}/faculty/{facultyId}")
    public void setFaculty(@PathVariable long studentId, @PathVariable long facultyId) {
        studentService.setFaculty(studentId, facultyService.getFaculty(facultyId));
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение факультета студента по id студента")
    @GetMapping(value = "/{id}/faculty")
    public Faculty getFaculty(@PathVariable long id) {
        return studentService.getStudent(id).getFaculty();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Сброс факультета для студента studentId")
    @PatchMapping(value = "/{id}/faculty/reset")
    public void resetFaculty(@PathVariable long id) {
        studentService.resetFaculty(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение списка всех студентов")
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск студентов по точному совпадению возраста")
    @GetMapping(value = "/filter/age/{age}")
    public List<Student> findStudentsByAge(@PathVariable int age) {
        return studentService.findStudentsByAge(age);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск студентов по диапазону возраста")
    @GetMapping(value = "/filter/age/between")
    public List<Student> findStudentsByAgeBetween(int fromAge, int toAge) {
        return studentService.findStudentsByAgeBetween(fromAge, toAge);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение количества студентов")
    @GetMapping(value = "/stat/count")
    public Long getCountOfStudents() {
        return studentService.getCountOfStudents();
    }

    /**
     * Шаг 2 (Модификация уже существующего эндпоинта)
     * <p>
     * Создать эндпоинт, который будет возвращать средний возраст всех студентов. Для получения информации о всех
     * студентах опять же следует использовать метод репозитория findAll()
     *
     * @return средний возраст всех студентов
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение среднего возраста студентов")
    @GetMapping(value = "/stat/age/average")
    public Double getAverageAgeOfStudents() {
        return studentService.getAverageAgeOfStudents();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение последних :limit добавленных студентов")
    @GetMapping(value = "/stat/last/{limit}")
    public List<Student> getLastStudentsById(@PathVariable int limit) {
        return studentService.getLastStudentsById(limit);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление n студентов для тестов. Возвращает количество добавленных студентов")
    @PostMapping("/test/add")
    public Integer addTestStudents(int count,
                                   int minNameLength, int maxNameLength,
                                   int minAge, int maxAge) {

        return studentService.addTestStudents(count, minNameLength, maxNameLength, minAge, maxAge);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление студентов, созданных для тестов")
    @DeleteMapping("/test/delete")
    public void deleteTestStudents() {
        studentService.removeTestStudents();
    }

    /**
     * Шаг 1
     * <p>
     * Добавить эндпоинт для получения всех имен всех студентов, чье имя начинается с буквы А. В ответе должен
     * находиться отсортированный в алфавитном порядке список с именами в верхнем регистре.
     * Для получения всех студентов из базы использовать метод репозитория findAll().
     *
     * @param letter начальный символ имени
     * @return список с именами студентов
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение списка студентов с именами на букву letter. Возвращает отсортированный список " +
            "имён в верхнем регистре")
    @GetMapping(value = "/starts/with/{letter}")
    public List<String> getStudentsByLetter(@PathVariable String letter) {
        return studentService.getStudentNames(letter);
    }

    /**
     * Шаг 1
     * <p>
     * Создать в StudentController эндпоинт GET /students/print-parallel.
     * <p>
     * Эндпоинт должен выводить в консоль имена всех студентов в параллельном режиме, а именно:
     * <p>
     * первые два имени вывести в основном потоке
     * имена третьего и четвертого студента вывести в параллельном потоке
     * имена пятого и шестого студента вывести в еще одном параллельном потоке.
     * Для вывода используйте команду System.out.println().
     * <p>
     * В итоге в консоли должен появиться список из шести имен в порядке, возможно отличном от порядка в коллекции.
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Вывод [ограниченного] списка студентов в консоль в параллельном режиме")
    @GetMapping(value = "/print-parallel")
    public String printStudentsParallel() {
        return studentService.printStudents(false);
    }

    /**
     * Шаг 2
     * <p>
     * Создать в StudentController эндпоинт GET /students/print-synchronized.
     * <p>
     * Эндпоинт должен выводить в консоль имена всех студентов в синхронном режиме.
     * <p>
     * Для этого создайте отдельный синхронизированный метод для вывода имен в консоль.
     * <p>
     * Далее необходимо, используя ранее созданный синхронизированный метод :
     * <p>
     * первые два имени вывести в основном потоке
     * имена третьего и четвертого студента в параллельном потоке
     * имена пятого и шестого студента в еще одном параллельном потоке
     * В итоге в консоли должен появиться список из шести имен в порядке, возможно отличном от порядка в коллекции
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Вывод [ограниченного] списка студентов в консоль в параллельном режиме, ограниченном " +
            "синхронизированным методом печати")
    @GetMapping(value = "/print-synchronized")
    public String printStudentsSynchronized() {
        return studentService.printStudents(true);
    }
}
