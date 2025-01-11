// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller.advice;

import lombok.Value;

/**
 * Описание ошибки.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.4
 */
@Value
public class ErrorData {
    int code;
    String message;
}
