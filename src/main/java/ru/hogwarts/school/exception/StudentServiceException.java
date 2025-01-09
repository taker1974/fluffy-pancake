// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception;

/**
 * Исключение, возникающее при работе с сервисом студентов.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class StudentServiceException extends RuntimeException {

    public StudentServiceException(String message) {
        super(message);
    }
}
