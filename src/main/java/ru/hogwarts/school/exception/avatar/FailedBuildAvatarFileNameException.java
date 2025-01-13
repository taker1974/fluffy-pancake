// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.exception.avatar;

/**
 * Исключение, которое выбрасывается при ошибке сборки уникального имени аватара.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class FailedBuildAvatarFileNameException extends RuntimeException {

    public static final int CODE = 649;

    public FailedBuildAvatarFileNameException() {
        super("Не удалось собрать уникальное имя аватара");
    }
}
