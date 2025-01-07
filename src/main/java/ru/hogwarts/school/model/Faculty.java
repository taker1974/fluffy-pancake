// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Факультет.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@SuppressWarnings("java:S6206")
@Entity
public final class Faculty {

    public static final int MIN_COLOR_NAME_LENGTH = 1;
    public static final int MAX_COLOR_NAME_LENGTH = 100;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @NotNull
    private final String name;

    @NotNull
    private final String color;

    public Faculty(Long id, String name, String color) throws FacultyException {

        if (id == null) {
            throw new FacultyException("Id не может быть null");
        }

        if (name == null) {
            throw new FacultyException("Имя не может быть null");
        }
        name = name.trim();
        if (name.isEmpty() || name.length() > MAX_COLOR_NAME_LENGTH) {
            throw new FacultyException("Длина имени должна быть от " +
                    MIN_COLOR_NAME_LENGTH + " до " + MAX_COLOR_NAME_LENGTH + "символов");
        }

        if (color == null) {
            throw new FacultyException("Цвет не может быть null");
        }
        color = color.trim();
        if (color.isEmpty() || color.length() > MAX_COLOR_NAME_LENGTH) {
            throw new FacultyException("Длина названия цвета должна быть от " +
                    MIN_COLOR_NAME_LENGTH + " до " + MAX_COLOR_NAME_LENGTH + "символов");
        }

        this.id = id;
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Faculty that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @NonNull
    public Long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getColor() {
        return color;
    }
}
