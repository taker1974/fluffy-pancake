package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.avatar.AvatarNotFoundException;
import ru.hogwarts.school.exception.avatar.IOAvatarFileException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping(value = "/avatar")
@Tag(name = "Аватары студентов")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AvatarController {

    private final AvatarService avatarService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузка аватара студента. Возвращает id аватара")
    @PostMapping(value = "/student/{studentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long uploadAvatar(@PathVariable long studentId, @RequestParam("file") MultipartFile avatar) {
        return avatarService.uploadAvatar(studentId, avatar).getId();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление аватара студента")
    @DeleteMapping(value = "/student/{studentId}/delete")
    public void deleteAvatar(@PathVariable long studentId) {
        avatarService.deleteAvatar(studentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Загрузка аватара студента из базы данных")
    @GetMapping(value = "/student/db/{studentId}")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable long studentId) {

        final Avatar avatar = avatarService.getAvatar(studentId).orElseThrow(AvatarNotFoundException::new);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(avatar.getData());
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Загрузка аватара студента из файла")
    @GetMapping(value = "/student/file/{studentId}")
    public void downloadAvatarFromFile(@PathVariable long studentId, HttpServletResponse response) {

        final Avatar avatar = avatarService.getAvatar(studentId).orElseThrow(AvatarNotFoundException::new);

        final Path path = Path.of(avatar.getFilePath());
        try {
            try (
                    InputStream is = Files.newInputStream(path);
                    OutputStream os = response.getOutputStream()
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

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех аватарок")
    @GetMapping
    public List<Avatar> getAllAvatars() {
        return avatarService.getAllAvatars();
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех аватарок постранично. Нумерация страниц - от 1")
    @GetMapping("/pages")
    public Page<Avatar> getAllAvatarsPaginated(@RequestParam int page, @RequestParam int size) {
        return avatarService.getAllAvatarsPaginated(page - 1, size);
    }
}
