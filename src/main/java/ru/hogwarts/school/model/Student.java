// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hogwarts.school.exception.StudentException;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
@Entity
@Data
@NoArgsConstructor
public class Student {

    public static final int MIN_AGE = 6;
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 100;

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private int age;

    /**
     * Конструктор.
     *
     * @param id   идентификатор студента
     * @param name имя студента
     * @param age  возраст студента
     * @throws StudentException если имя или возраст не может быть null, если имя длиннее MAX_NAME_LENGTH символов,
     *                          если возраст меньше MIN_AGE лет
     */
    public Student(long id, String name, int age) {

        if (name == null) {
            throw new StudentException("Имя не может быть null");
        }
        name = name.trim();
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            throw new StudentException("Длина имени должна быть от " + MIN_NAME_LENGTH + " до " + MAX_NAME_LENGTH +
                    "символов");
        }

        if (age < MIN_AGE) {
            throw new StudentException("Возраст не может быть меньше " + MIN_AGE + " лет");
        }

        this.id = id;
        this.name = name;
        this.age = age;
    }
}
