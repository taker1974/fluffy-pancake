// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.exception.faculty.BadFacultyColorException;
import ru.hogwarts.school.exception.faculty.BadFacultyNameException;
import ru.hogwarts.school.tools.StringEx;

import java.util.Objects;
import java.util.Set;

/**
 * Факультет.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.5
 */
@Getter
@Entity
@NoArgsConstructor
public class Faculty {

    public static final int MIN_FACULTY_NAME_LENGTH = 1;
    public static final int MAX_FACULTY_NAME_LENGTH = 100;

    public static final int MIN_COLOR_NAME_LENGTH = 3;
    public static final int MAX_COLOR_NAME_LENGTH = 100;

    @Setter
    @Id
    @GeneratedValue
    private long id;

    private String name;

    /**
     * Устанавливает название факультета.
     *
     * @param name название факультета
     * @throws BadFacultyNameException если название не удовлетворяет условиям
     */
    public void setName(String name) {
        if (StringEx.isMeaningful(name, MIN_FACULTY_NAME_LENGTH, MAX_FACULTY_NAME_LENGTH)) {
            throw new BadFacultyNameException(MIN_FACULTY_NAME_LENGTH, MAX_FACULTY_NAME_LENGTH);
        }
        this.name = name;
    }

    private String color;

    /**
     * Устанавливает "цвет" факультета.
     *
     * @param color "цвет" факультета
     * @throws BadFacultyColorException если название цвета не удовлетворяет условиям
     */
    public void setColor(String color) {
        if (!StringEx.isMeaningful(color, MIN_COLOR_NAME_LENGTH, MAX_COLOR_NAME_LENGTH)) {
            throw new BadFacultyColorException(MIN_COLOR_NAME_LENGTH, MAX_COLOR_NAME_LENGTH);
        }
        this.color = color;
    }

    @OneToMany(mappedBy = "faculty")
    @JsonIgnoreProperties("faculty") // устранение цикличности при формировании JSON
    private Set<Student> students;

    /**
     * Конструктор.<br>
     * Вызываются методы {@link #setName(String)} и {@link #setColor(String)}, которые выбрасывают исключения.
     *
     * @param id    идентификатор факультета
     * @param name  название факультета
     * @param color "цвет" факультета
     */
    public Faculty(long id, String name, String color) {
        setName(name);
        setColor(color);

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Faculty faculty = (Faculty) o;
        return id == faculty.id &&
                Objects.equals(name, faculty.name) &&
                Objects.equals(color, faculty.color) &&
                Objects.equals(students, faculty.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, students);
    }
}
