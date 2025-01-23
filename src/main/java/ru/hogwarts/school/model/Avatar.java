package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Avatar {

    // https://sysout.ru/otnoshenie-onetoone-v-hibernate/
    // https://sky.pro/wiki/java/reshenie-problemy-s-fetch-type-lazy-v-jpa-i-hibernate/

    // как всё-таки решить проблему с lazy fetch?

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
