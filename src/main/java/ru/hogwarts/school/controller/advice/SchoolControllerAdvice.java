// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;

/**
 * Обработка ошибок контроллера работы со студентами.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@ControllerAdvice
public class SchoolControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorData> handleException(RuntimeException e) {

        if (e instanceof NullPointerException ex) {
            return handleNullPointerException(ex);
        } else if (e instanceof IllegalArgumentException ex) {
            return handleIllegalArgumentException(ex);
        } else if (e instanceof FacultyNotFoundException ex) {
            return handleFacultyNotFoundException(ex);
        } else if (e instanceof StudentNotFoundException ex) {
            return handleStudentNotFoundException(ex);
        }
        return handleRuntimeExceptionDefault(e);
    }

    public ResponseEntity<ErrorData> handleRuntimeExceptionDefault(RuntimeException e) {

        var code = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorData(code.value(), e.getLocalizedMessage()), code);
    }

    public ResponseEntity<ErrorData> handleNullPointerException(NullPointerException e) {

        var code = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(new ErrorData(code.value(), e.getLocalizedMessage()), code);
    }

    public ResponseEntity<ErrorData> handleIllegalArgumentException(IllegalArgumentException e) {

        var code = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<>(new ErrorData(code.value(), e.getLocalizedMessage()), code);
    }

    public ResponseEntity<ErrorData> handleFacultyNotFoundException(FacultyNotFoundException e) {

        var code = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorData(code.value(), e.getLocalizedMessage()), code);
    }

    public ResponseEntity<ErrorData> handleStudentNotFoundException(StudentNotFoundException e) {

        var code = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorData(code.value(), e.getLocalizedMessage()), code);
    }
}
