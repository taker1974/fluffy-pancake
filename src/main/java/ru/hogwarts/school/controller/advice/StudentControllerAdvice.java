package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StudentControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadStudentAgeException.class)
    public ResponseEntity<ErrorResponse> handleBadStudentAgeException(BadStudentAgeException e) {
        return new ResponseEntity<>(new ErrorResponse(BadStudentAgeException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(BadStudentNameException.class)
    public ResponseEntity<ErrorResponse> handleBadStudentNameException(BadStudentNameException e) {
        return new ResponseEntity<>(new ErrorResponse(BadStudentNameException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NullStudentException.class)
    public ResponseEntity<ErrorResponse> handleNullStudentException(NullStudentException e) {
        return new ResponseEntity<>(new ErrorResponse(NullStudentException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleStudentAlreadyExistsException(StudentAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponse(StudentAlreadyExistsException.CODE, e.getMessage()),
                HttpStatus.CONFLICT);
    }

    // Это не работает - возникает зацикливание на представлении
//    @ResponseStatus(HttpStatus.CONFLICT)
//    @ExceptionHandler(StudentAlreadyExistsException.class)
//    public ErrorResponse handleStudentAlreadyExistsException(StudentAlreadyExistsException e) {
//        return new ErrorResponse(StudentAlreadyExistsException.CODE, e.getMessage());
//    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(StudentNotFoundException.CODE, e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
