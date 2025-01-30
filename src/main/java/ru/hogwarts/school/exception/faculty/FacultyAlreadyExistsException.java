package ru.hogwarts.school.exception.faculty;

public class FacultyAlreadyExistsException extends RuntimeException {

    public static final int CODE = 752;

    public FacultyAlreadyExistsException() {
        super("Такой факультет уже существует");
    }
}
