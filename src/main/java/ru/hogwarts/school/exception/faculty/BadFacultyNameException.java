package ru.hogwarts.school.exception.faculty;

public class BadFacultyNameException extends RuntimeException {

    public static final int CODE = 390;

    public BadFacultyNameException(int minNameLength, int maxNameLength) {
        super(String.format(
                "Название факультета не должно быть null и " +
                "длина названия должна быть от %d до %d символов", minNameLength, maxNameLength));
    }
}
