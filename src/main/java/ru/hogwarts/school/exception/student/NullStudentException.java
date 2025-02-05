package ru.hogwarts.school.exception.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullStudentException extends RuntimeException {

    public static final int CODE = 807;

    public NullStudentException() {
        super("Параметр Student не должен быть null");
    }
}
