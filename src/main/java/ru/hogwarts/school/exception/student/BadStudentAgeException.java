// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.student;

/**
 * Исключение по возрасту студента.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class BadStudentAgeException extends RuntimeException {

    public static final int CODE = 527;

    public BadStudentAgeException(int minAge) {
        super(String.format("Возраст студента должен быть не менее %d лет", minAge));
    }
}
