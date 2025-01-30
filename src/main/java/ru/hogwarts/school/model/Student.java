package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private int age;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    public Student(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.age = student.getAge();
        this.faculty = student.getFaculty();
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        id = 0;
        faculty = null;
    }

    public Student setNew() {
        id = 0;
        faculty = null;
        return this;
    }
}
