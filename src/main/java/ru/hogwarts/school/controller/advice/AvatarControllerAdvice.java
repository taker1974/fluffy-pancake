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

@ControllerAdvice
public class AvatarControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadAvatarDataException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarDataException(BadAvatarDataException e,
                                                                      WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarDataException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadAvatarFileNameException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarFileNameException(BadAvatarFileNameException e,
                                                                          WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarFileNameException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadAvatarSizeException.class)
    public ResponseEntity<ErrorResponse> handleBadAvatarSizeException(BadAvatarSizeException e,
                                                                      WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadAvatarSizeException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailedBuildAvatarFileNameException.class)
    public ResponseEntity<ErrorResponse> handleFailedBuildAvatarFileNameException(FailedBuildAvatarFileNameException e,
                                                                                  WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(FailedBuildAvatarFileNameException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOAvatarFileException.class)
    public ResponseEntity<ErrorResponse> handleIOAvatarFileException(IOAvatarFileException e,
                                                                     WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(IOAvatarFileException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullAvatarFileException.class)
    public ResponseEntity<ErrorResponse> handleNullAvatarFileException(NullAvatarFileException e,
                                                                       WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(NullAvatarFileException.CODE, e.getMessage(), e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
