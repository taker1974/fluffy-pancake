package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = "/avatar")
@Tag(name = "Аватары студентов")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    // Здесь post потому, что всегда создаётся новый аватар, новый ресурс на сервере.
    @Operation(summary = "Загрузка аватара студента")
    @PostMapping(value = "/student/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(@PathVariable long studentId,
                                               @RequestParam MultipartFile avatar) {
        return ResponseEntity.ok(avatarService.uploadAvatar(studentId, avatar));
    }

    @Operation(summary = "Удаление аватара студента")
    @DeleteMapping(value = "/student/{studentId}")
    public ResponseEntity<Optional<Avatar>> deleteAvatar(@PathVariable long studentId) {
        return ResponseEntity.ok(avatarService.deleteAvatar(studentId));
    }

    @Operation(summary = "Загрузка аватара студента из базы данных")
    @GetMapping(value = "/student/db/{studentId}")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable long studentId) {
        Optional<Avatar> avatar = avatarService.getAvatar(studentId);
        if (avatar.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.get().getMediaType()));
        headers.setContentLength(avatar.get().getData().length);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.get().getData());
    }

    @Operation(summary = "Загрузка аватара студента из файла")
    @GetMapping(value = "/student/file/{studentId}")
    public void downloadAvatarFromFile(@PathVariable long studentId, HttpServletResponse response) {

        final Optional<Avatar> optionalAvatar = avatarService.getAvatar(studentId);
        if (optionalAvatar.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final Avatar avatar = optionalAvatar.get();

        final Path path = Path.of(avatar.getFilePath());
        try {
            try (
                    InputStream is = Files.newInputStream(path);
                    OutputStream os = response.getOutputStream();
            ) {
                response.setContentLength((int) avatar.getFileSize());
                response.setContentType(avatar.getMediaType());
                response.setStatus(HttpServletResponse.SC_OK);

                is.transferTo(os);
            }
        } catch (Exception e) {
            throw new IOAvatarFileException();
        }
    }

    @Operation(summary = "Получение всех аватаров")
    @GetMapping
    public Collection<Avatar> getAllAvatars() {
        return avatarService.getAllAvatars();
    }
}
