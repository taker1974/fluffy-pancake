package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByColorIgnoreCase(String color);

    @Query(value = "SELECT f FROM faculty f WHERE LOWER(f.name) = LOWER(:name) OR LOWER(f.color) = LOWER(:color)",
            nativeQuery = true)
    List<Faculty> findByNameOrColorIgnoreCase(String name, String color);
}
