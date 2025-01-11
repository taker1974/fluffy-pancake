// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.faculty;

/**
 * Исключение, возникающее тогда, когда Faculty равен null.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
public class NullFacultyException extends RuntimeException {

    public static final int CODE = 141;

    public NullFacultyException() {
        super("Параметр Faculty не должен быть null");
    }
}
