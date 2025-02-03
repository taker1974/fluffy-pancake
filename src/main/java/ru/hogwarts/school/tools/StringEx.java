package ru.hogwarts.school.tools;

import java.util.Optional;

public final class StringEx {

    private StringEx() {
    }

    /**
     * @return null || isEmpty || isBlank
     */
    public static boolean isNullOrWhitespace(String str) {
        return str == null || str.isBlank();
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

    /**
     * Замена вхождений в строке вида "{text}" на предложенные подстроки.
     * Пример: для параметра str = "{host}:{port}/{app}" и параметров "localhost", 8080 и "school"
     * будет возвращено значение "localhost:8080/school".
     *
     * @param str     строка вида "http://{host}:{port}/{app}"
     * @param objects подстроки для поочерёдной замены
     * @return строка вида "http://{host}:{port}/{app}"
     */
    public static String replace(String str, Object... objects) {
        for (Object object : objects) {
            str = str.replaceFirst("\\{[^}]*}", object.toString());
        }
        return str;
    }
}
