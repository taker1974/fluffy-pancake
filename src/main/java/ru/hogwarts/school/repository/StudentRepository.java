// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.Optional;

/**
 * Репозиторий студентов.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.7
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN FETCH s.faculty WHERE s.id=:id")
    Optional<Student> findWithJoinFetch(long id);

    @Query("SELECT s FROM Student s WHERE s.age = :age")
    Collection<Student> findByAge(int age);

    @Query("SELECT s FROM Student s WHERE s.age BETWEEN :fromAge AND :toAge")
    Collection<Student> findByAgeBetween(int fromAge, int toAge);
}
