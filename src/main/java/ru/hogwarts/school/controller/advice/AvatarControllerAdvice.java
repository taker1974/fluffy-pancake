package ru.hogwarts.school.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.avatar.BadAvatarDataException;
import ru.hogwarts.school.exception.avatar.BadAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.BadAvatarSizeException;
import ru.hogwarts.school.exception.avatar.FailedBuildAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.exception.avatar.NullAvatarFileException;

@ControllerAdvice
public class AvatarControllerAdvice extends AbstractBaseControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadAvatarDataException.class)
    public ErrorResponse handleBadAvatarDataException(BadAvatarDataException e) {
        return new ErrorResponse(BadAvatarDataException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadAvatarFileNameException.class)
    public ErrorResponse handleBadAvatarFileNameException(BadAvatarFileNameException e) {
        return new ErrorResponse(BadAvatarFileNameException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadAvatarSizeException.class)
    public ErrorResponse handleBadAvatarSizeException(BadAvatarSizeException e) {
        return new ErrorResponse(BadAvatarSizeException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FailedBuildAvatarFileNameException.class)
    public ErrorResponse handleFailedBuildAvatarFileNameException(FailedBuildAvatarFileNameException e) {
        return new ErrorResponse(FailedBuildAvatarFileNameException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOAvatarFileException.class)
    public ErrorResponse handleIOAvatarFileException(IOAvatarFileException e) {
        return new ErrorResponse(IOAvatarFileException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullAvatarFileException.class)
    public ErrorResponse handleNullAvatarFileException(NullAvatarFileException e) {
        return new ErrorResponse(NullAvatarFileException.CODE, e.getMessage());
    }
}
