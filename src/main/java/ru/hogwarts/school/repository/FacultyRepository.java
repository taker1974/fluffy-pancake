// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

/**
 * Репозиторий факультетов.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.7
 */
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    @Query("SELECT f FROM Faculty f WHERE f.color = :color")
    Collection<Faculty> findByColor(String color);

    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) = LOWER(:name) OR LOWER(f.color) = LOWER(:color)")
    Collection<Faculty> findByNameOrColorIgnoreCase(String name, String color);

    @Query("SELECT s FROM Student s WHERE s.faculty = :facultyId")
    Collection<Student> findStudents(long facultyId);
}
