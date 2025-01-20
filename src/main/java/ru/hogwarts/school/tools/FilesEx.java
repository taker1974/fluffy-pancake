// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.tools;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

/**
 * Инструменты для работы с файлами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class FilesEx {

    public static final int MAX_FILE_NAME_LENGTH = 1024;

    private FilesEx() {
    }

    /**
     * @param file {@link MultipartFile}
     * @return null || empty
     */
    public static boolean isNullOrEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    /**
     * @param filePath путь к файлу в виде "resource1://resource2/.../fileName.ext"
     * @return имя файла без пути и без расширения
     */
    public static Optional<String> getFileNamePart(@NotNull String filePath) {
        try {
            String fileName = new File(filePath).getName();
            return Optional.of(fileName.substring(0, fileName.lastIndexOf('.')));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * @param filePath путь к файлу в виде "resource1://resource2/.../fileName.ext"
     * @return расширение файла с точкой
     */
    public static Optional<String> getFileExtensionPart(@NotNull String filePath) {
        try {
            String fileName = new File(filePath).getName();
            return Optional.of(fileName.substring(fileName.lastIndexOf('.')));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Каким образом при создании уникального имени файла будет<br>
     * использоваться уникальная часть:<br>
     * - будет второй, после оригинального имени файла<br>
     * - будет первой, а затем будет идти оригинальное имя файла<br>
     * - будет единственной частью имени файла.
     */
    public enum UniqueFileNamePolicy {
        SALT_LAST,
        SALT_FIRST,
        SALT_ONLY
    }

    /**
     * @param filePath путь к файлу в виде "resource1://resource2/.../fileName.ext"
     * @param salt     уникальная часть имени файла
     * @return уникальное имя файла
     */
    public static Optional<String> buildUniqueFileName(@NotNull String filePath,
                                                       @NotNull String salt, UniqueFileNamePolicy policy) {
        try {
            String fileName = new File(filePath).getName();
            String name = fileName.substring(0, fileName.lastIndexOf('.'));
            String ext = fileName.substring(fileName.lastIndexOf('.'));
            return switch (policy) {
                case SALT_LAST -> Optional.of(name + salt + ext);
                case SALT_FIRST -> Optional.of(salt + name + ext);
                case SALT_ONLY -> Optional.of(salt + ext);
            };
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
