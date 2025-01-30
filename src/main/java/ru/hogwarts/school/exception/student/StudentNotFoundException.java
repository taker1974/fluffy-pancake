package ru.hogwarts.school.exception.student;

public class StudentNotFoundException extends RuntimeException {

    public static final int CODE = 784;

    public StudentNotFoundException() {
        super("Студент не найден");
    }
}
