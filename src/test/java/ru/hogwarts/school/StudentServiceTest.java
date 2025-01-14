// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * StudentServiceTest.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentServiceTest {

    @LocalServerPort
    private int port;

    @Test
    void test() {
        Assertions.assertTrue(port > 0);
    }
}
