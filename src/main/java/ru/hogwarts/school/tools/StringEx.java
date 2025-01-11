// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.tools;

/**
 * Инструменты для работы со строками.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public final class StringEx {

    /**
     * Конструктор.
     */
    private StringEx() {
    }

    /**
     * @param str строка
     * @return null || isEmpty || isBlank
     */
    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    /**
     * @param str - строка
     * @return null || isEmpty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * @param str       строка
     * @param minLength минимальная длина строки
     * @param maxLength максимальная длина строки
     * @return null || isEmpty || isBlank
     */
    public static boolean isMeaningful(String str, int minLength, int maxLength) {
        return !isNullOrWhitespace(str) &&
                str.length() >= minLength &&
                str.length() <= maxLength;
    }
}
