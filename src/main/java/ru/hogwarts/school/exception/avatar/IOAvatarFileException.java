// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, возникающее при работе с файлом аватара.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class IOAvatarFileException extends RuntimeException {

    public static final int CODE = 873;

    public IOAvatarFileException() {
        super("Ошибка при работе с файлом аватара");
    }
}
