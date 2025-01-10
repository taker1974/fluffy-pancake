// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.4
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

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    /**
     * Конструктор.
     *
     * @param id   идентификатор студента
     * @param name имя студента
     * @param age  возраст студента
     * @throws NullPointerException     если имя студента равно null
     * @throws IllegalArgumentException если имя студента короче MIN_NAME_LENGTH или длиннее MAX_NAME_LENGTH символов
     */
    public Student(long id, String name, int age) {

        if (name == null) {
            throw new NullPointerException("Имя студента не может быть null");
        }
        if (name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Длина имени студента должна быть от " + MIN_NAME_LENGTH + " до " + MAX_NAME_LENGTH + " символов");
        }

        if (age < MIN_AGE) {
            throw new IllegalArgumentException("Возраст студента не может быть меньше " + MIN_AGE + " лет");
        }

        this.id = id;
        this.name = name;
        this.age = age;
    }
}
