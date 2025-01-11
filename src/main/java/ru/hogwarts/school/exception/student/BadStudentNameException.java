// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.student;

/**
 * Исключение по имени студента.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
public class BadStudentNameException extends RuntimeException {

    public static final int CODE = 153;

    public BadStudentNameException(int minNameLength, int maxNameLength) {
        super(String.format("Имя студента не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minNameLength, maxNameLength));
    }
}
