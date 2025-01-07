// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Faculty;

/**
 * FacultiesRepository.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public interface FacultiesRepository extends JpaRepository<Faculty, Long> {
}
