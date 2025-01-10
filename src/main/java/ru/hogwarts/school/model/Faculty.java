// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Факультет.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.4
 */
@Entity
@Data
@NoArgsConstructor
public class Faculty {

    public static final int MIN_FACULTY_NAME_LENGTH = 1;
    public static final int MAX_FACULTY_NAME_LENGTH = 100;

    public static final int MIN_COLOR_NAME_LENGTH = 1;
    public static final int MAX_COLOR_NAME_LENGTH = 100;

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String color;

    @OneToMany(mappedBy = "faculty")
    private Set<Student> students;

    /**
     * Конструктор.
     *
     * @param id    идентификатор факультета
     * @param name  название факультета
     * @param color "цвет" факультета
     * @throws NullPointerException     если название факультета равно null или если "цвет" факультета равен null
     * @throws IllegalArgumentException если название факультета короче MIN_FACULTY_NAME_LENGTH или длиннее
     *                                  MAX_FACULTY_NAME_LENGTH или если "цвет" факультета короче
     *                                  MIN_COLOR_NAME_LENGTH или длиннее MAX_COLOR_NAME_LENGTH
     */
    public Faculty(long id, String name, String color) {

        if (name == null) {
            throw new NullPointerException("Название факультета не может быть null");
        }
        if (name.isBlank() || name.length() > MAX_FACULTY_NAME_LENGTH) {
            throw new IllegalArgumentException("Длина имени должна быть от " +
                    MIN_FACULTY_NAME_LENGTH + " до " + MAX_FACULTY_NAME_LENGTH + " символов");
        }

        if (color == null) {
            throw new NullPointerException("\"Цвет\" факультета не может быть null");
        }
        if (color.isBlank() || color.length() > MAX_COLOR_NAME_LENGTH) {
            throw new IllegalArgumentException("Длина названия \"цвета\" факультета должна быть от " +
                    MIN_COLOR_NAME_LENGTH + " до " + MAX_COLOR_NAME_LENGTH + " символов");
        }

        this.id = id;
        this.name = name;
        this.color = color;
    }
}
