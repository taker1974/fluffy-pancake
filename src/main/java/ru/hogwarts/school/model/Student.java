// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed", "java:S6206"})
@Entity
public class Student {

    public static final int MIN_AGE = 6;
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 100;

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private int age;

    public Student() {
    }

    public Student(long id, String name, int age) throws StudentException {

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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
