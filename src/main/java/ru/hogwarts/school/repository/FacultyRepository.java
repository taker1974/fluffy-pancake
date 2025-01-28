package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByColorIgnoreCase(String color);

    @Query("SELECT f FROM Faculty f WHERE lower(f.name) = lower(:name) OR lower(f.color) = lower(:color)")
    List<Faculty> findByNameOrColorIgnoreCase(String name, String color);
}
