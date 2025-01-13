// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, возникающее тогда, когда размер в MultipartFile
 * меньше или больше заданных значений.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class BadAvatarSizeException extends RuntimeException {

    public static final int CODE = 677;

    public BadAvatarSizeException(long avatarSizeMin, long avatarSizeMax) {
        super("Размер файла аватара должен быть от " + avatarSizeMin + " до " + avatarSizeMax + " байт");
    }
}
