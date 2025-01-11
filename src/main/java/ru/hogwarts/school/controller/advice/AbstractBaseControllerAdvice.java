// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller.advice;

/**
 * Абстрактный предок обработчиков исключений.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public abstract class AbstractBaseControllerAdvice {

    public static final String MESSAGE_PREFIX = "Перехвачено исключение";

    protected String getCommonMessage(Object obj) {
        return String.format("%s %s", MESSAGE_PREFIX, obj.getClass().getSimpleName());
    }
}
