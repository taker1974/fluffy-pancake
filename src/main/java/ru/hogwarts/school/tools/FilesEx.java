// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.tools;

import org.springframework.web.multipart.MultipartFile;

/**
 * Инструменты для работы с файлами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class FilesEx {

    private FilesEx() {}

    public static boolean isNullOrEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }
}
