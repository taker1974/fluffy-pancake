// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.exception.faculty.BadFacultyColorException;
import ru.hogwarts.school.exception.faculty.BadFacultyNameException;
import ru.hogwarts.school.tools.StringEx;

import java.util.Objects;

/**
 * Факультет.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.6
 */
@Getter
@Entity
@NoArgsConstructor
public class Faculty {

    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 100;

    public static final int MIN_COLOR_LENGTH = 3;
    public static final int MAX_COLOR_LENGTH = 100;

    @Setter
    @Id
    @GeneratedValue
    private long id;

    private String name;

    /**
     * @throws BadFacultyNameException если название не удовлетворяет условиям
     */
    public void setName(String name) {
        this.name = StringEx.getMeaningful(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)
                .orElseThrow(() -> new BadFacultyNameException(MIN_NAME_LENGTH, MAX_NAME_LENGTH));
    }

    private String color;

    /**
     * @throws BadFacultyColorException если название "цвета" не удовлетворяет условиям
     */
    public void setColor(String color) {
        this.color = StringEx.getMeaningful(color, MIN_COLOR_LENGTH, MAX_COLOR_LENGTH)
                .orElseThrow(() -> new BadFacultyColorException(MIN_COLOR_LENGTH, MAX_COLOR_LENGTH));
    }

    /**
     * Вызываются методы {@link #setName(String)} и {@link #setColor(String)}, которые выбрасывают исключения.
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
                Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
