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
import ru.hogwarts.school.exception.avatar.BadAvatarDataException;
import ru.hogwarts.school.exception.avatar.BadAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.BadAvatarSizeException;
import ru.hogwarts.school.exception.avatar.FailedBuildAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.exception.avatar.NullAvatarFileException;

/**
 * Обработка исключений работы с аватарками.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.6
 */
@ControllerAdvice
public class AvatarControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadAvatarDataException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarDataException(BadAvatarDataException e,
                                                                      WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarDataException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadAvatarFileNameException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarFileNameException(BadAvatarFileNameException e,
                                                                          WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarFileNameException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadAvatarSizeException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarSizeException(BadAvatarSizeException e,
                                                                      WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarSizeException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailedBuildAvatarFileNameException.class)
    public ResponseEntity<ErrorResponse> handleFailedBuildAvatarFileNameException(FailedBuildAvatarFileNameException e,
                                                                                  WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(FailedBuildAvatarFileNameException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOAvatarFileException.class)
    public ResponseEntity<ErrorResponse> handleIOAvatarFileException(IOAvatarFileException e,
                                                                     WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(IOAvatarFileException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullAvatarFileException.class)
    public ResponseEntity<ErrorResponse> handleNullAvatarFileException(NullAvatarFileException e,
                                                                       WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(NullAvatarFileException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
