package ru.hogwarts.school.exception.faculty;

public class NullFacultyException extends RuntimeException {

    public static final int CODE = 141;

    public NullFacultyException() {
        super("Параметр Faculty не должен быть null");
    }
}
