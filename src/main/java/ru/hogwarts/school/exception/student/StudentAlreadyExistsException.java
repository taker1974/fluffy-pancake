// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.student;

/**
 * Исключение по поиску студента.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
public class StudentAlreadyExistsException extends RuntimeException {

    public static final int CODE = 220;

    public StudentAlreadyExistsException() {
        super("Такой студент уже существует");
    }
}
