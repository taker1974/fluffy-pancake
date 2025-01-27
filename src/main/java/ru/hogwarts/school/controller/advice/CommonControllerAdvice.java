package ru.hogwarts.school.controller.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import ru.hogwarts.school.dto.ErrorResponse;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CommonControllerAdvice extends AbstractBaseControllerAdvice {

    public static final String SERVER_ERROR = "Ошибка сервера";

    public static final int E_CODE = 160;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ErrorResponse handleException(Exception e, WebRequest request) {
        return new ErrorResponse(E_CODE, SERVER_ERROR, e.getMessage());
    }

    public static final int RTE_CODE = 427;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    public ErrorResponse handleRuntimeException(RuntimeException e, WebRequest request) {
        return new ErrorResponse(RTE_CODE, SERVER_ERROR, e.getMessage());
    }

    public static final int NPE_CODE = 467;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 2)
    public ErrorResponse handleNpe(NullPointerException e) {
        return new ErrorResponse(NPE_CODE, getCommonMessage(e), e.getMessage());
    }

    public static final int IAE_CODE = 881;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @Order(Ordered.LOWEST_PRECEDENCE - 3)
    public ErrorResponse handleIAE(IllegalArgumentException e) {
        return new ErrorResponse(IAE_CODE, getCommonMessage(e), e.getMessage());
    }
}
