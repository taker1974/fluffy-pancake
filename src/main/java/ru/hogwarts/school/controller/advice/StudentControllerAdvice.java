package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StudentControllerAdvice extends AbstractBaseControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadStudentAgeException.class)
    public ErrorResponse handleBadStudentAgeException(BadStudentAgeException e) {
        return new ErrorResponse(BadStudentAgeException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadStudentNameException.class)
    public ErrorResponse handleBadStudentNameException(BadStudentNameException e) {
        return new ErrorResponse(BadStudentNameException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NullStudentException.class)
    public ErrorResponse handleNullStudentException(NullStudentException e) {
        return new ErrorResponse(NullStudentException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ErrorResponse handleStudentAlreadyExistsException(StudentAlreadyExistsException e) {
        return new ErrorResponse(StudentAlreadyExistsException.CODE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentNotFoundException.class)
    public ErrorResponse handleStudentNotFoundException(StudentNotFoundException e) {
        return new ErrorResponse(StudentNotFoundException.CODE, e.getMessage());
    }
}
