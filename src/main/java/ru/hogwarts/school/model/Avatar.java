package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mediaType", "data", "student"})
public class Avatar {

    @Id
    private Long id;

    private String filePath;
    private long fileSize;
    private String mediaType;

    @JsonIgnore
    private byte[] data;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Student student;
}
