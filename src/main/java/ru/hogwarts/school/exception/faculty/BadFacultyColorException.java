// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.faculty;

/**
 * Исключение по "цвету" факультета.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
public class BadFacultyColorException extends RuntimeException {

    public static final int CODE = 195;

    public BadFacultyColorException(int minColorLength, int maxColorLength) {
        super(String.format(
                "\"Цвет\" факультета не должен быть null и " +
                "длина этой строки должна быть от %d до %d символов", minColorLength, maxColorLength));
    }
}
