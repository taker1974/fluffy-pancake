package ru.hogwarts.school.exception.faculty;

public class FacultyNotFoundException extends RuntimeException {

    public static final int CODE = 682;

    public FacultyNotFoundException() {
        super("Факультет не найден");
    }
}
