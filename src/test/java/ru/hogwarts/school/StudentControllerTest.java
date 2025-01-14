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
 * StudentControllerTest.<br>
 * <p>
 * Из условий ДЗ<br>
 * <a href="https://my.sky.pro/student-cabinet/stream-lesson/145842/homework-requirements">SkyPro</a><br>
 * <a href="https://skyengpublic.notion.site/3-6-Spring-Boot-0070e5697e594bd0a5c6e5f96a29f950">Notion</a>:<br>
 * Шаг 1<br>
 * Создать класс для тестирования в пакете test.<br>
 * Создать по одному тесту на каждый эндпоинт контроллера StudentController, используя TestRestTemplate.<br>
 * Критерии оценки:<br>
 * - в пакете test создан класс для тестирования StudentController;<br>
 * - для тестирования использовался TestRestTemplate;<br>
 * - для каждого эндпоинта контроллера StudentController создан как минимум один тест.<br>
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Test
    void test() {
        Assertions.assertTrue(port > 0);
    }
}
