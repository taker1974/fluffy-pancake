package ru.hogwarts.school.tools;

import java.util.Random;

public class NameGenerator {

    private NameGenerator() {
    }

    protected static final char[] VOWELS_LOWER = "aeiou".toCharArray();
    protected static final char[] VOWELS_UPPER = "AEIOU".toCharArray();
    protected static final char[] CONSONANTS_LOWER = "bcdfghjklmnpqrstvwxyz".toCharArray();
    protected static final char[] CONSONANTS_UPPER = "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();

    private static final Random random = new Random();

    public static String getName(int minLength, int maxLength, String suffix) {

        if (minLength <= 0 || minLength > maxLength) {
            throw new IllegalArgumentException("Invalid minLength or maxLength");
        }

        int length = random.nextInt(minLength, maxLength);
        var name = new StringBuilder(length);

        boolean isPrevVowel = random.nextBoolean();
        name.append(isPrevVowel ? VOWELS_UPPER[random.nextInt(VOWELS_UPPER.length)]
                : CONSONANTS_UPPER[random.nextInt(CONSONANTS_UPPER.length)]);

        for (int i = 1; i < length; i++) {
            name.append(isPrevVowel ? CONSONANTS_LOWER[random.nextInt(CONSONANTS_LOWER.length)]
                    : VOWELS_LOWER[random.nextInt(VOWELS_LOWER.length)]);
            isPrevVowel = !isPrevVowel;
        }

        if (suffix != null && !suffix.isEmpty()) {
            name.append(suffix);
        }

        return name.toString();
    }
}
