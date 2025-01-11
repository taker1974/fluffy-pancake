// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.hogwarts.school.dto.ErrorResponse;

/**
 * Обработка общих исключений.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.6
 */
@ControllerAdvice
public class CommonControllerAdvice extends AbstractBaseControllerAdvice {

    public static final String SERVER_ERROR = "Ошибка сервера";

    public static final int E_CODE = 160;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(E_CODE, SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int RTE_CODE = 427;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(RTE_CODE, SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int NPE_CODE = 467;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNpe(NullPointerException e) {
        return new ResponseEntity<>(
                new ErrorResponse(NPE_CODE, getCommonMessage(e)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int IAE_CODE = 881;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(
                new ErrorResponse(IAE_CODE, getCommonMessage(e)), HttpStatus.BAD_REQUEST);
    }
}
