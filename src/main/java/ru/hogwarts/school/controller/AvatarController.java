// SkyPro
// Терских Константин, kostus.online.1974@yandex.ru, 2025
// Домашнее задание третьего курса ("Работа с кодом") Java Developer.

package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

/**
 * Контроллер для работы с аватарками.
 *
 * @author Константин Терских, kostus.online.1974@yandex.ru, 2025
 * @version 0.3
 */
@RestController
@RequestMapping(value = "/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(@PathVariable long studentId,
                                               @RequestParam MultipartFile avatar) {
        return ResponseEntity.ok(avatarService.uploadAvatar(studentId, avatar));
    }

    @GetMapping(value = "/db/{studentId}")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable long studentId) {
        Avatar avatar = avatarService.getAvatar(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/file/{studentId}")
    public void downloadAvatarFromFile(@PathVariable long studentId, HttpServletResponse response) {
        Avatar avatar = avatarService.getAvatar(studentId);
        try{
            avatarService.transferTo(response, avatar);
        } catch (Exception e) {
            throw new IOAvatarFileException();
        }
    }
}
