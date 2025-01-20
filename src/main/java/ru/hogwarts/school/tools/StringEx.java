package ru.hogwarts.school.tools;

import java.util.Optional;

public final class StringEx {

    private StringEx() {}

    /**
     * @return null || isEmpty || isBlank
     */
    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    /**
     * @return null || isEmpty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * @return null || isEmpty || isBlank
     */
    public static Optional<String> getMeaningful(String str, int minLength, int maxLength) {
        if (!isNullOrWhitespace(str) && str.length() >= minLength && str.length() <= maxLength) {
            return Optional.of(str);
        }
        return Optional.empty();
    }
}
