package ru.hogwarts.school.exception.student;

public class StudentAlreadyExistsException extends RuntimeException {

    public static final int CODE = 220;

    public StudentAlreadyExistsException() {
        super("Такой студент уже существует");
    }
}
