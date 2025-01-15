// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.hogwarts.school.dto.ErrorResponse;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.exception.student.NullStudentException;
import ru.hogwarts.school.exception.student.StudentAlreadyExistsException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;

/**
 * Обработка исключений работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.6
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StudentControllerAdvice extends AbstractBaseControllerAdvice {

    @ExceptionHandler(BadStudentAgeException.class)
    public ResponseEntity<ErrorResponse> handleBadStudentAgeException(BadStudentAgeException e,
                                                                      WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadStudentAgeException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadStudentNameException.class)
    public ResponseEntity<ErrorResponse> handleBadStudentNameException(BadStudentNameException e,
                                                                       WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(BadStudentNameException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullStudentException.class)
    public ResponseEntity<ErrorResponse> handleNullStudentException(NullStudentException e,
                                                                    WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(NullStudentException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleStudentAlreadyExistsException(StudentAlreadyExistsException e,
                                                                             WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(StudentAlreadyExistsException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException e,
                                                                        WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(StudentNotFoundException.CODE, e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
