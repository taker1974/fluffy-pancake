// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.avatar.BadAvatarDataException;
import ru.hogwarts.school.exception.avatar.BadAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.BadAvatarSizeException;
import ru.hogwarts.school.exception.avatar.FailedBuildAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.exception.avatar.NullAvatarFileException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.tools.FilesEx;
import ru.hogwarts.school.tools.StringEx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Сервис аватарок.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
@Service
public class AvatarService {

    @Value("${avatars.path}")
    private String avatarsPath;

    @Value("${avatar.size.min}")
    private long avatarSizeMin;

    @Value("${avatar.size.max}")
    private long avatarSizeMax;

    @Value("${avatar.io.buffer.size}")
    private int avatarIoBufferSize;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    /**
     * Загрузка аватара на сервер в БД и в файл.
     *
     * @param studentId  ID студента
     * @param avatarFile файл аватара
     * @return аватар
     * @throws StudentNotFoundException, NullAvatarFileException, BadAvatarSizeException,
     *                                   BadAvatarFileNameException, IOAvatarFileException, BadAvatarDataException,
     *                                   FailedBuildAvatarFileNameException, IOAvatarFileException,
     *                                   BadAvatarDataException, BadAvatarDataException
     */
    public Avatar uploadAvatar(long studentId, MultipartFile avatarFile) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new StudentNotFoundException();
        }

        if (avatarFile == null) {
            throw new NullAvatarFileException();
        }

        long avatarSize = avatarFile.getSize();
        if (avatarFile.isEmpty() || avatarSize < avatarSizeMin || avatarSize > avatarSizeMax) {
            throw new BadAvatarSizeException(avatarSizeMin, avatarSizeMax);
        }

        String originalFileName = avatarFile.getOriginalFilename();
        if (!StringEx.isMeaningful(originalFileName, 1, FilesEx.MAX_FILE_NAME_LENGTH)) {
            throw new BadAvatarFileNameException(1, FilesEx.MAX_FILE_NAME_LENGTH);
        }

        Optional<String> fileName = FilesEx.buildUniqueFileName(originalFileName,
                "-" + UUID.randomUUID(), FilesEx.UniqueFileNamePolicy.SALT_LAST);
        if (fileName.isEmpty()) {
            throw new FailedBuildAvatarFileNameException();
        }

        Path filePath = Path.of(avatarsPath, fileName.get());
        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);
            try (
                    InputStream is = avatarFile.getInputStream();
                    OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                    BufferedInputStream bis = new BufferedInputStream(is, avatarIoBufferSize);
                    BufferedOutputStream bos = new BufferedOutputStream(os, avatarIoBufferSize);
            ) {
                bis.transferTo(bos);
            }
        } catch (Exception e) {
            throw new IOAvatarFileException();
        }

        Avatar avatar = avatarRepository.findById(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());

        try {
            avatar.setData(avatarFile.getBytes());
        } catch (IOException e) {
            throw new BadAvatarDataException();
        }

        avatarRepository.save(avatar);
        return avatar;
    }

    /**
     * Получение аватара из БД по ID студента.
     *
     * @param idByStudent ID студента
     * @return аватар
     */
    public Avatar getAvatar(long idByStudent) {
        return avatarRepository.findById(idByStudent).orElse(null);
    }

    /**
     * Передача потока данных из аватара клиенту.
     *
     * @param avatar   аватар
     * @param response {@link HttpServletResponse}
     * @throws IOException в случае ошибок в потоках данных
     */
    public void transfer(Avatar avatar, HttpServletResponse response) throws IOException {
        Path path = Path.of(avatar.getFilePath());
        try (
                InputStream is = Files.newInputStream(path);
                OutputStream os = response.getOutputStream();
        ) {
            is.transferTo(os);

            response.setContentLength((int) avatar.getFileSize());
            response.setContentType(avatar.getMediaType());
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
