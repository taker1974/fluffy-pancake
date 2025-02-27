package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.avatar.BadAvatarDataException;
import ru.hogwarts.school.exception.avatar.BadAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.BadAvatarSizeException;
import ru.hogwarts.school.exception.avatar.FailedBuildAvatarFileNameException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.exception.avatar.NullAvatarFileException;
import ru.hogwarts.school.exception.student.StudentNotFoundException;
import ru.hogwarts.school.exception.avatar.AvatarNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.tools.FilesEx;
import ru.hogwarts.school.tools.LogEx;
import ru.hogwarts.school.tools.StringEx;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@RequiredArgsConstructor
public class AvatarService {

    public static final String STUDENT_ID_PREFIX = "studentId = ";

    Logger log = LoggerFactory.getLogger(AvatarService.class);

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

    @Transactional
    public Avatar uploadAvatar(long studentId, MultipartFile avatarFile) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, STUDENT_ID_PREFIX + studentId);

        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        if (avatarFile == null) {
            throw new NullAvatarFileException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(),
                "avatarFile.getOriginalFilename() = " + avatarFile.getOriginalFilename());

        long avatarSize = avatarFile.getSize();
        if (avatarFile.isEmpty() || avatarSize < avatarSizeMin || avatarSize > avatarSizeMax) {
            throw new BadAvatarSizeException(avatarSizeMin, avatarSizeMax);
        }

        final String originalFileName = StringEx.getMeaningful(avatarFile.getOriginalFilename(),
                        1, FilesEx.MAX_FQN_LENGTH)
                .orElseThrow(() -> new BadAvatarFileNameException(1, FilesEx.MAX_FQN_LENGTH));

        // Получим уникальное имя файла из оригинального имени и уникальной строки (UUID).
        // Для оригинального имени файла originalFileName.ext уникальное имя файла будет выглядеть так:
        // "originalFileName-UUID.ext"
        // См. UniqueFileNamePolicy.
        final String fileName = FilesEx.buildUniqueFileName(originalFileName,
                        "-" + UUID.randomUUID(), FilesEx.UniqueFileNamePolicy.SALT_LAST)
                .orElseThrow(FailedBuildAvatarFileNameException::new);

        Optional<Avatar> optionalAvatar = avatarRepository.findByStudentId(studentId);

        if (optionalAvatar.isPresent()) {
            try {
                Files.deleteIfExists(Path.of(optionalAvatar.get().getFilePath()));
            } catch (IOException e) {
                throw new IOAvatarFileException();
            }
        }

        final Avatar avatar = optionalAvatar.orElseGet(Avatar::new);

        Path filePath = Path.of(avatarsPath, fileName);

        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setStudent(student);

        try {
            avatar.setData(avatarFile.getBytes());
        } catch (IOException e) {
            throw new BadAvatarDataException();
        }

        avatarRepository.save(avatar);

        try {
            Files.createDirectories(filePath.getParent());
            Files.deleteIfExists(filePath);
            try (
                    InputStream is = avatarFile.getInputStream();
                    OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                    BufferedInputStream bis = new BufferedInputStream(is, avatarIoBufferSize);
                    BufferedOutputStream bos = new BufferedOutputStream(os, avatarIoBufferSize)
            ) {
                bis.transferTo(bos);
            }
        } catch (Exception e) {
            throw new IOAvatarFileException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return avatar;
    }

    public Optional<Avatar> getAvatar(long studentId) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN, STUDENT_ID_PREFIX + studentId);
        return avatarRepository.findByStudentId(studentId);
    }

    @Transactional
    public void deleteAvatar(long studentId) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, STUDENT_ID_PREFIX + studentId);

        final Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(AvatarNotFoundException::new);

        avatarRepository.delete(avatar);

        try {
            Files.deleteIfExists(Path.of(avatar.getFilePath()));
        } catch (IOException e) {
            throw new IOAvatarFileException();
        }

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPED);
    }

    @Transactional
    public List<Avatar> getAllAvatars() {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.SHORT_RUN);
        return avatarRepository.findAll();
    }

    @Transactional
    public Page<Avatar> getAllAvatarsPaginated(int page, int size) {
        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STARTING, "page = " + page + ", size = " + size);

        PageRequest pageRequest = PageRequest.of(page, size);

        LogEx.trace(log, LogEx.getThisMethodName(), LogEx.STOPPING);
        return avatarRepository.findAll(pageRequest);
    }
}
