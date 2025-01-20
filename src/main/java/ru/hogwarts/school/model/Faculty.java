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
@ToString(exclude = "students")
@RequiredArgsConstructor
@AllArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String color;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnore
    private Set<Student> students;
}
