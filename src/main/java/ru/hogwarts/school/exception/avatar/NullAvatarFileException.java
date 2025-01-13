// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, возникающее тогда, когда MultipartFile при загрузке равен null.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class NullAvatarFileException extends RuntimeException {

    public static final int CODE = 483;

    public NullAvatarFileException() {
        super("Параметр MultipartFile не должен быть null");
    }
}
