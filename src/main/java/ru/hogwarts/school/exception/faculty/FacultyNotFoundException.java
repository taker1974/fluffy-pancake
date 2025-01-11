// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.faculty;

/**
 * Исключение по поиску факультета.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
public class FacultyNotFoundException extends RuntimeException {

    public static final int CODE = 682;

    public FacultyNotFoundException() {
        super("Факультет не найден");
    }
}
