package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.faculty.BadFacultyColorException;
import ru.hogwarts.school.exception.faculty.BadFacultyNameException;
import ru.hogwarts.school.exception.faculty.FacultyAlreadyExistsException;
import ru.hogwarts.school.exception.faculty.FacultyNotFoundException;
import ru.hogwarts.school.exception.faculty.NullFacultyException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FacultyControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadFacultyColorException.class)
    public ResponseEntity<ErrorResponse> handleBadFacultyColorException(BadFacultyColorException e,
                                                                        WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadFacultyColorException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadFacultyNameException.class)
    public ResponseEntity<ErrorResponse> handleBadFacultyNameException(BadFacultyNameException e,
                                                                       WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadFacultyNameException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FacultyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFacultyNotFoundException(FacultyAlreadyExistsException e,
                                                                        WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(FacultyAlreadyExistsException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFacultyNotFoundException(FacultyNotFoundException e,
                                                                        WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(FacultyNotFoundException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullFacultyException.class)
    public ResponseEntity<ErrorResponse> handleNullFacultyException(NullFacultyException e,
                                                                    WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(NullFacultyException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
