package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.hogwarts.school.dto.ErrorResponse;

import java.util.Arrays;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CommonControllerAdvice extends AbstractBaseControllerAdvice {

    public static final int E_CODE = 160;

    @ExceptionHandler(Exception.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse(E_CODE, e.getMessage(), Arrays.toString(e.getStackTrace())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int RTE_CODE = 427;

    @ExceptionHandler(RuntimeException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(RTE_CODE, e.getMessage(), Arrays.toString(e.getStackTrace())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int NPE_CODE = 467;

    @ExceptionHandler(NullPointerException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 2)
    public ResponseEntity<ErrorResponse> handleNpe(NullPointerException e) {
        return new ResponseEntity<>(new ErrorResponse(NPE_CODE, e.getMessage(), Arrays.toString(e.getStackTrace())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static final int IAE_CODE = 881;

    @ExceptionHandler(IllegalArgumentException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 3)
    public ResponseEntity<ErrorResponse> handleIAE(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorResponse(IAE_CODE, e.getMessage(), Arrays.toString(e.getStackTrace())),
                HttpStatus.NOT_ACCEPTABLE);
    }
}
