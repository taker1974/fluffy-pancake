package ru.hogwarts.school.exception.avatar;

public class BadAvatarFileNameException extends RuntimeException {

    public static final int CODE = 22;

    public BadAvatarFileNameException(int minFileNameLength, int maxFileNameLength) {
        super(String.format("Имя файла не должно быть null и " +
                "длина имени должна быть от %d до %d символов", minFileNameLength, maxFileNameLength));
    }
}
