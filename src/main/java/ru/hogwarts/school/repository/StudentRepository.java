package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN FETCH s.faculty WHERE s.id=:id")
    Optional<Student> findWithJoinFetch(long id);

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int fromAge, int toAge);
}
