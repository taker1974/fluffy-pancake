// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;

/**
 * Студент.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.5
 */
@Getter
@Entity
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
     * Устанавливает имя студента.
     *
     * @param name имя студента
     * @throws BadStudentNameException если имя не удовлетворяет условиям
     */
    public void setName(String name) {
        if (name != null && !name.isBlank() && name.length() <= MAX_NAME_LENGTH) {
            this.name = name;
        }
        throw new BadStudentNameException(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    }

    private int age;

    /**
     * Устанавливает возраст студента.
     * @param age возраст студента
     * @throws BadStudentAgeException если возраст не удовлетворяет условиям
     */
    public void setAge(int age) {
        if (age < MIN_AGE) {
            throw new BadStudentAgeException(MIN_AGE);
        }
        this.age = age;
    }

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    @JsonIgnoreProperties("students") // устранение цикличности при формировании JSON
    private Faculty faculty;

    /**
     * Конструктор.<br>
     * Выполняются {@link #setName(String)} и {@link #setAge(int)}, которые выбрасывают исключения.
     *
     * @param id   идентификатор студента
     * @param name имя студента
     * @param age  возраст студента
     */
    public Student(long id, String name, int age) {
        setName(name);
        setAge(age);

        this.id = id;
    }
}
