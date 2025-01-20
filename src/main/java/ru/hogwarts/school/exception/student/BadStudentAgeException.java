package ru.hogwarts.school.exception.student;

public class BadStudentAgeException extends RuntimeException {

    public static final int CODE = 527;

    public BadStudentAgeException(int minAge) {
        super(String.format("Возраст студента должен быть не менее %d лет", minAge));
    }
}
