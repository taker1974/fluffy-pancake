package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
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
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

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

    public AvatarService(StudentRepository studentRepository,
                         AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    @Transactional
    public Avatar uploadAvatar(long studentId, MultipartFile avatarFile) {

        // Сначала получим студента - без него всё остальное не имеет смысла.
        final Student student = studentRepository.findById(studentId)
                .orElseThrow(StudentNotFoundException::new);

        // Выполним простые проверки: решим, сможем ли мы обработать файл.

        if (avatarFile == null) {
            throw new NullAvatarFileException();
        }

        long avatarSize = avatarFile.getSize();
        if (avatarFile.isEmpty() || avatarSize < avatarSizeMin || avatarSize > avatarSizeMax) {
            throw new BadAvatarSizeException(avatarSizeMin, avatarSizeMax);
        }

        final String originalFileName = StringEx.getMeaningful(avatarFile.getOriginalFilename(),
                        1, FilesEx.MAX_FQN_LENGTH)
                .orElseThrow(() -> new BadAvatarFileNameException(1, FilesEx.MAX_FQN_LENGTH));

        // Получим уникальное имя файла.
        final String fileName = FilesEx.buildUniqueFileName(originalFileName,
                        "-" + UUID.randomUUID(), FilesEx.UniqueFileNamePolicy.SALT_LAST)
                .orElseThrow(FailedBuildAvatarFileNameException::new);

        // Проверки выполнены,
        // и теперь можно выполнять операции с БД и с файловой системой.

        // Пробуем получить аватар студента.
        Optional<Avatar> optionalAvatar = avatarRepository.findByStudentId(studentId);

        // Если аватар уже есть, то удалим его из файловой системы.
        if (optionalAvatar.isPresent()) {
            try {
                Files.deleteIfExists(Path.of(optionalAvatar.get().getFilePath()));
            } catch (IOException e) {
                throw new IOAvatarFileException();
            }
        }

        // Если аватар не был найден, то создадим его.
        final Avatar avatar = optionalAvatar.orElseGet(Avatar::new);

        Path filePath = Path.of(avatarsPath, fileName);

        // Сначала сохраним или обновим аватар в БД.
        try {
            avatar.setFilePath(filePath.toString());
            avatar.setFileSize(avatarFile.getSize());
            avatar.setMediaType(avatarFile.getContentType());
            avatar.setData(avatarFile.getBytes());
            avatar.setStudent(student);
        } catch (IOException e) {
            throw new BadAvatarDataException();
        }
        avatarRepository.save(avatar);

        // В последнюю очередь запишем новый файл аватара на диск.
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

        return avatar;
    }

    public Optional<Avatar> getAvatar(long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    @Transactional
    public Optional<Avatar> deleteAvatar(long studentId) {

        final Optional<Avatar> optionalAvatar = avatarRepository.findByStudentId(studentId);
        if (optionalAvatar.isEmpty()) {
            return Optional.empty();
        }
        final Avatar avatar = optionalAvatar.get();

        // Удаляем аватар из БД.
        avatarRepository.delete(avatar);

        // Удаляем аватар из файловой системы.
        try {
            Files.deleteIfExists(Path.of(avatar.getFilePath()));
        } catch (IOException e) {
            throw new IOAvatarFileException();
        }

        return Optional.of(avatar);
    }

    public Collection<Avatar> getAllAvatars() {
        return avatarRepository.findAll();
    }
}
