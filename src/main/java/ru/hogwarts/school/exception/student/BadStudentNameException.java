package ru.hogwarts.school.exception.student;

public class BadStudentNameException extends RuntimeException {

    public static final int CODE = 153;

    public BadStudentNameException(int minNameLength, int maxNameLength) {
        super(String.format("Имя студента не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minNameLength, maxNameLength));
    }
}
