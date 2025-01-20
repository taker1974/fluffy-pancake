// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.tools.StringEx;

import java.util.Objects;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.5
 */
@Entity
@Getter // геттеры для всех членов класса
@NoArgsConstructor
public class Student {

    public static final int MIN_AGE = 6;
    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 100;

    @Setter
    @Id
    @GeneratedValue
    private long id;

    private String name;

    /**
     * @throws BadStudentNameException если имя не удовлетворяет условиям
     */
    public void setName(String name) {
        this.name = StringEx.getMeaningful(name, MIN_NAME_LENGTH, MAX_NAME_LENGTH)
                .orElseThrow(() -> new BadStudentNameException(MIN_NAME_LENGTH, MAX_NAME_LENGTH));
    }

    private int age;

    /**
     * @throws BadStudentAgeException если возраст не удовлетворяет условиям
     */
    public void setAge(int age) {
        if (age < MIN_AGE) {
            throw new BadStudentAgeException(MIN_AGE);
        }
        this.age = age;
    }

    // https://sysout.ru/otnoshenie-manytoone-v-hibernate-i-spring/

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Faculty faculty;

    /**
     * Выполняются {@link #setName(String)} и {@link #setAge(int)}, которые выбрасывают исключения.
     */
    public Student(long id, String name, int age) {
        setName(name);
        setAge(age);

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return id == student.id &&
                age == student.age &&
                Objects.equals(name, student.name) &&
                Objects.equals(faculty, student.faculty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, faculty);
    }
}
