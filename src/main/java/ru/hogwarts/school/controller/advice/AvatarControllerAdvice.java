package ru.hogwarts.school.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.avatar.AvatarNotFoundException;
import ru.hogwarts.school.exception.avatar.BadAvatarDataException;
import ru.hogwarts.school.exception.avatar.BadAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.BadAvatarSizeException;
import ru.hogwarts.school.exception.avatar.FailedBuildAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.exception.avatar.NullAvatarFileException;

@ControllerAdvice
public class AvatarControllerAdvice extends AbstractBaseControllerAdvice {

    @SuppressWarnings("unused")
    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBadAvatarNotFoundException(AvatarNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(AvatarNotFoundException.CODE, e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(BadAvatarDataException.class)
    public ResponseEntity<ErrorResponseDto> handleBadAvatarDataException(BadAvatarDataException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadAvatarDataException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(BadAvatarFileNameException.class)
    public ResponseEntity<ErrorResponseDto> handleBadAvatarFileNameException(BadAvatarFileNameException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadAvatarFileNameException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(BadAvatarSizeException.class)
    public ResponseEntity<ErrorResponseDto> handleBadAvatarSizeException(BadAvatarSizeException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadAvatarSizeException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(FailedBuildAvatarFileNameException.class)
    public ResponseEntity<ErrorResponseDto> handleFailedBuildAvatarFileNameException(FailedBuildAvatarFileNameException e) {
        return new ResponseEntity<>(new ErrorResponseDto(FailedBuildAvatarFileNameException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(IOAvatarFileException.class)
    public ResponseEntity<ErrorResponseDto> handleIOAvatarFileException(IOAvatarFileException e) {
        return new ResponseEntity<>(new ErrorResponseDto(IOAvatarFileException.CODE, e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(NullAvatarFileException.class)
    public ResponseEntity<ErrorResponseDto> handleNullAvatarFileException(NullAvatarFileException e) {
        return new ResponseEntity<>(new ErrorResponseDto(NullAvatarFileException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
