package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int fromAge, int toAge);

    @Query(value = "SELECT COUNT(id) FROM student", nativeQuery = true)
    long getCountOfStudents();

    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    double getAverageAgeOfStudents();

    @Query(value = "SELECT * FROM student s ORDER BY s.id DESC LIMIT :limit", nativeQuery = true)
    List<Student> getLastStudentsById(Integer limit);

    @Query(value = "SELECT * FROM student s WHERE lower(s.name) LIKE lower(%:suffix)", nativeQuery = true)
    List<Student> findByNameSuffix(String suffix);
}
