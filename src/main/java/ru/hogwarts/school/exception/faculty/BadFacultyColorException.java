package ru.hogwarts.school.exception.faculty;

public class BadFacultyColorException extends RuntimeException {

    public static final int CODE = 195;

    public BadFacultyColorException(int minColorLength, int maxColorLength) {
        super(String.format(
                "\"Цвет\" факультета не должен быть null и " +
                "длина этой строки должна быть от %d до %d символов", minColorLength, maxColorLength));
    }
}
