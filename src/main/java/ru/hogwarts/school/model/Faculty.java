package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "students")
public class Faculty {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String color;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnore
    private Set<Student> students;

    public Faculty(String name, String color) {
        this.name = name;
        this.color = color;

        id = 0;
        students = null;
    }

    public Faculty(Faculty faculty) {
        id = faculty.getId();
        name = faculty.getName();
        color = faculty.getColor();
        students = faculty.getStudents();
    }
}
