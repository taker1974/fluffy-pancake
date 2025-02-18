package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.dto.ErrorResponseDto;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StudentControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadStudentAgeException.class)
    public ResponseEntity<ErrorResponseDto> handleBadStudentAgeException(BadStudentAgeException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadStudentAgeException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(BadStudentNameException.class)
    public ResponseEntity<ErrorResponseDto> handleBadStudentNameException(BadStudentNameException e) {
        return new ResponseEntity<>(new ErrorResponseDto(BadStudentNameException.CODE, e.getMessage()),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NullStudentException.class)
    public ResponseEntity<ErrorResponseDto> handleNullStudentException(NullStudentException e) {
        return new ResponseEntity<>(new ErrorResponseDto(NullStudentException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleStudentAlreadyExistsException(StudentAlreadyExistsException e) {
        return new ResponseEntity<>(new ErrorResponseDto(StudentAlreadyExistsException.CODE, e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleStudentNotFoundException(StudentNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(StudentNotFoundException.CODE, e.getMessage()),
                HttpStatus.NOT_FOUND);
    }
}
