// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Avatar;

import java.util.Optional;

/**
 * Репозиторий аватарок студентов.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.7
 */
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    @Query("SELECT a FROM Avatar a WHERE a.student.id = :studentId")
    Optional<Avatar> findByStudentId(Long studentId);
}
