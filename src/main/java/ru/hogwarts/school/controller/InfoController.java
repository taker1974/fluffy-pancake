package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.tools.LogEx;

@RestController
@RequestMapping(value = "/info")
@Tag(name = "Информация о сервере")
@RequiredArgsConstructor
public class InfoController {

    Logger log = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private int serverPort;

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение порта приложения")
    @GetMapping("/port")
    public Integer getApplicationPort() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, "serverPort = " + serverPort);
        return serverPort;
    }
}
