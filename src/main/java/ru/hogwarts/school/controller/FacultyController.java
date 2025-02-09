package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/faculty")
@Tag(name = "Факультеты")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового факультета. Возвращает id нового факультета")
    @PostMapping("/add")
    public Long addFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty).getId();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение существующего факультета по id")
    @GetMapping(value = "/{id}")
    public Faculty getFaculty(@PathVariable long id) {
        return facultyService.getFaculty(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление существующего факультета")
    @PutMapping("/update")
    public Faculty updateFaculty(@RequestBody Faculty faculty) {
        return facultyService.updateFaculty(faculty);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление существующего факультета")
    @DeleteMapping(value = "/delete/{id}")
    public void deleteFaculty(@PathVariable long id) {
        facultyService.deleteFaculty(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск факультетов по точному совпадению \"цвета\"")
    @GetMapping(value = "/filter/color")
    public List<Faculty> findFacultiesByColor(String color) {
        return facultyService.findFacultiesByColor(color);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск факультетов по названию или по \"цвету\" без учёта регистра")
    @GetMapping(value = "/filter")
    public List<Faculty> findFacultiesByNameOrColor(String name, String color) {
        return facultyService.findFacultiesByNameOrColorIgnoreCase(name, color);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех факультетов")
    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение студентов факультета")
    @GetMapping(value = "/{facultyId}/students")
    public Set<Student> findStudentsByFaculty(@PathVariable long facultyId) {
        return facultyService.getFaculty(facultyId).getStudents();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление n факультетов для тестов. Возвращает количество добавленных факультетов")
    @PostMapping("/test/add")
    public Integer addTestFaculties(int count,
                                    int minNameLength, int maxNameLength) {

        return facultyService.addTestFaculties(count, minNameLength, maxNameLength);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление факультетов, созданных для тестов")
    @DeleteMapping("/test/delete")
    public void deleteTestFaculties() {
        facultyService.removeTestFaculties();
    }

    /**
     * Шаг 3
     * <p>
     * Создать эндпоинт, который будет возвращать самое длинное название факультета.
     *
     * @return список строк результата
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение самых длинных названий факультетов")
    @GetMapping(value = "/filter/names/longest")
    public List<String> getLongestFacultyNames() {

        List<String> results = new ArrayList<>(50);

        long s = System.nanoTime();
        String name = facultyService.getLongestNameDumb();
        double t = (System.nanoTime() - s) / 1E6;
        results.add("Самое длинное название факультета, тупой способ:");
        results.add(String.format("  %d символов, %s", name.length(), name));
        results.add(String.format("  время поиска: %f мс", t));
        results.add("");

        s = System.nanoTime();
        name = facultyService.getLongestName();
        t = (System.nanoTime() - s) / 1E6;
        results.add("Самое длинное название факультета, менее тупой способ:");
        results.add(String.format("  %d символов, %s", name.length(), name));
        results.add(String.format("  время поиска: %f мс", t));
        results.add("");

        return results;
    }

    /**
     * В ДЗ содержится ошибка переполнения int:
     * <p>
     * int sum = Stream.iterate(1, a -> a +1) .limit(1_000_000) .reduce(0, (a, b) -> a + b );
     * Сумма этого ряда - 500 000 500 000, что превышает максимально допустимое значение int.
     * <p>
     * Правильное условие для ряда больше 1М:
     * <p>
     * long sum = Stream.iterate(1L, a -> a + 1L).limit(1_000_000).reduce(0L, (a, b) -> a + b );
     * или
     * long sum = Stream.iterate(1L, a -> a + 1L).limit(1_000_000).reduce(0L, Long::sum);
     * <p>
     * Шаг 4
     * <p>
     * Создать эндпоинт (не важно в каком контроллере), который будет возвращать целочисленное значение. Это значение
     * вычисляется следующей формулой:
     * <p>
     * int sum = Stream.iterate(1, a -> a +1) .limit(1_000_000) .reduce(0, (a, b) -> a + b );
     * Необходимо придумать способ уменьшить время ответа эндпоинта путем модификации вышеописанного выражения.
     *
     * @return список строк результата
     */
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение суммы ряда")
    @GetMapping(value = "/test/sum")
    public List<String> getSums() {

        List<String> results = new ArrayList<>(50);
        long limit = 1_000_000;

        long s = System.nanoTime();
        double value = facultyService.getSumDumb(limit);
        double t = (System.nanoTime() - s) / 1E6;
        results.add("Сумма ряда от 1 до " + limit + ", тупой способ: " + value);
        results.add(String.format("  время вычисления: %f мс", t));
        results.add("");

        s = System.nanoTime();
        value = facultyService.getSum(limit);
        t = (System.nanoTime() - s) / 1E6;
        results.add("Сумма ряда от 1 до " + limit + ", менее тупой способ: " + value);
        results.add(String.format("  время вычисления: %f мс", t));

        return results;
    }
}
