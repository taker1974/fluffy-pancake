// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@SuppressWarnings({"LombokGetterMayBeUsed", "java:S6206"})
@Entity
public final class Student {

    public static final int MIN_AGE = 6;
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 100;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @NotNull
    private final String name;

    private final int age;

    public Student(Long id, String name, int age) throws StudentException {

        if (id == null) {
            throw new StudentException("Id не может быть null");
        }

        if (name == null) {
            throw new StudentException("Имя не может быть null");
        }
        name = name.trim();
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            throw new StudentException("Длина имени должна быть от " +
                    MIN_NAME_LENGTH + " до " + MAX_NAME_LENGTH + "символов");
        }

        if (age < MIN_AGE) {
            throw new StudentException("Возраст не может быть меньше " + MIN_AGE + " лет");
        }

        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student that)) {
            return false;
        }
        return age == that.age &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
