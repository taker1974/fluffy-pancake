// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, возникающее при попытке прочитать массив байт из аватара.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class BadAvatarDataException extends RuntimeException {

    public static final int CODE = 405;

    public BadAvatarDataException() {
        super("Не удалось прочитать массив байт из аватара.");
    }
}
