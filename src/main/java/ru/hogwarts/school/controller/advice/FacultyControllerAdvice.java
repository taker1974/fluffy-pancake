package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.faculty.BadFacultyColorException;
import ru.hogwarts.school.exception.faculty.BadFacultyNameException;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FacultyControllerAdvice extends AbstractBaseControllerAdvice {

    @SuppressWarnings("unused")
    @ExceptionHandler(BadFacultyColorException.class)
    public ResponseEntity<ErrorResponseDto> handleBadFacultyColorException(BadFacultyColorException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadFacultyColorException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(BadFacultyNameException.class)
    public ResponseEntity<ErrorResponseDto> handleBadFacultyNameException(BadFacultyNameException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadFacultyNameException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(FacultyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleFacultyAlreadyExistsException(FacultyAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponseDto(FacultyAlreadyExistsException.CODE, e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleFacultyNotFoundException(FacultyNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(FacultyNotFoundException.CODE, e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(NullFacultyException.class)
    public ResponseEntity<ErrorResponseDto> handleNullFacultyException(NullFacultyException e) {
        return new ResponseEntity<>(new ErrorResponseDto(NullFacultyException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
