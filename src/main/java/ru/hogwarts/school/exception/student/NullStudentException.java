// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.student;

/**
 * Исключение, возникающее тогда, когда Student равен null.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
public class NullStudentException extends RuntimeException {

    public static final int CODE = 807;

    public NullStudentException() {
        super("Параметр Student не должен быть null");
    }
}
