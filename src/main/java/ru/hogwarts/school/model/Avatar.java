// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

/**
 * Аватар студента.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.2
 */
@Entity
@NoArgsConstructor
@Setter
@Getter
public class Avatar {

    @Id
    @GeneratedValue
    private Long id;

    private String filePath;
    private long fileSize;

    private String mediaType;

    private byte[] data;

    @OneToOne
    private Student student;

    // Замечание:
    // SonarQube не рекомендует использовать @Data вместе с @Entity

    public Avatar(long id,
                  String filePath, long fileSize, String mediaType,
                  byte[] data,
                  Student student) {
        this.id = id;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.data = data;
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Avatar avatar = (Avatar) o;
        return fileSize == avatar.fileSize &&
                Objects.equals(id, avatar.id) &&
                Objects.equals(filePath, avatar.filePath) &&
                Objects.equals(mediaType, avatar.mediaType) &&
                Objects.deepEquals(data, avatar.data) &&
                Objects.equals(student, avatar.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filePath, fileSize, mediaType, Arrays.hashCode(data), student);
    }
}
