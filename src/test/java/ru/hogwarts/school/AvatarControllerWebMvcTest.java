package ru.hogwarts.school;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.controller.advice.AvatarControllerAdvice;
import ru.hogwarts.school.controller.advice.CommonControllerAdvice;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ActiveProfiles("test-h2")
@ContextConfiguration(classes = {AvatarController.class,
        CommonControllerAdvice.class, AvatarControllerAdvice.class,
        AvatarService.class,
        AvatarRepository.class, StudentRepository.class,
        Avatar.class, Student.class})
@WebMvcTest(AvatarController.class)
class AvatarControllerWebMvcTest extends SchoolControllerBaseTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AvatarRepository avatarRepository;

    @SuppressWarnings("unused")
    @MockitoBean
    StudentRepository studentRepository;

    @SuppressWarnings("unused")
    @InjectMocks
    AvatarController avatarController;

    @Test
    @DisplayName("Получение всех аватарок постранично -> возвращается Page со списком аватарок")
    void whenGetAllAvatarsPaginated_thenReturnsExpectedPage() throws Exception {

        final int length = 10;
        final Avatar[] avatars = new Avatar[length];
        Arrays.fill(avatars, new Avatar());

        when(avatarRepository.findAll(any(Pageable.class))).thenReturn(
                new PageImpl<>(
                        Arrays.asList(avatars),
                        PageRequest.of(0, 3),
                        avatars.length));

        mvc.perform(MockMvcRequestBuilders
                        .get("/avatar/pages?page=1&size=3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(avatars.length))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(4));

        when(avatarRepository.findAll(any(Pageable.class))).thenReturn(
                new PageImpl<>(
                        Collections.emptyList(),
                        PageRequest.of(0, 3),
                        0));

        mvc.perform(MockMvcRequestBuilders
                        .get("/avatar/pages?page=1&size=3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(0));
    }
}
