// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception;

/**
 * Исключение, возникающее при работе со студентом.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class StudentException extends RuntimeException {

    public StudentException(String message) {
        super(message);
    }
}
