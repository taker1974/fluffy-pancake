package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.faculty.BadFacultyColorException;
import ru.hogwarts.school.exception.faculty.BadFacultyNameException;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;

import java.util.Arrays;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FacultyControllerAdvice extends AbstractBaseControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadFacultyColorException.class)
    public ErrorResponse handleBadFacultyColorException(BadFacultyColorException e) {
        return new ErrorResponse(BadFacultyColorException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadFacultyNameException.class)
    public ErrorResponse handleBadFacultyNameException(BadFacultyNameException e) {
        return new ErrorResponse(BadFacultyNameException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FacultyAlreadyExistsException.class)
    public ErrorResponse handleFacultyNotFoundException(FacultyAlreadyExistsException e) {
        return new ErrorResponse(FacultyAlreadyExistsException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FacultyNotFoundException.class)
    public ErrorResponse handleFacultyNotFoundException(FacultyNotFoundException e) {
        return new ErrorResponse(FacultyNotFoundException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullFacultyException.class)
    public ErrorResponse handleNullFacultyException(NullFacultyException e) {
        return new ErrorResponse(NullFacultyException.CODE, e.getMessage());
    }
}
