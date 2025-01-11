// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.faculty;

/**
 * Исключение по названию факультета.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
public class BadFacultyNameException extends RuntimeException {

    public static final int CODE = 390;

    public BadFacultyNameException(int minNameLength, int maxNameLength) {
        super(String.format(
                "Название факультета не должно быть null и " +
                "длина названия должна быть от %d до %d символов", minNameLength, maxNameLength));
    }
}
