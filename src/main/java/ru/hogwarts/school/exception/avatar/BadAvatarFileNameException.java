// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, возникающее тогда, когда длина имени файла в MultipartFile
 * меньше или больше заданных значений.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class BadAvatarFileNameException extends RuntimeException {

    public static final int CODE = 22;

    public BadAvatarFileNameException(int minFileNameLength, int maxFileNameLength) {
        super(String.format("Имя файла не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minFileNameLength, maxFileNameLength));
    }
}
