package oleborn.testresearch.moduletest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import oleborn.testresearch.controller.UserAppController;
import oleborn.testresearch.exception.AccessDeniedException;
import oleborn.testresearch.exception.GlobalExceptionHandler;
import oleborn.testresearch.feignclient.UserFeignClient;
import oleborn.testresearch.mapper.UserAppMapperImpl;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.moduletest.enums.EnumCase;
import oleborn.testresearch.repository.UserAppRepository;
import oleborn.testresearch.service.UserAppServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest
@ContextConfiguration(
        classes = {
                UserAppServiceImpl.class,
                UserAppMapperImpl.class,
                UserAppController.class,
                GlobalExceptionHandler.class
        }
)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private UserFeignClient userFeignClient;

    @Test
    @DisplayName("Тест успешно выполняющий взаимодействие контроллера с сервисов")
    @SneakyThrows
    public void userCreateControllerTest() {

        //given
        UserAppDto userAppDto = new UserAppDto("Den", "test@mail.ru");

        Mockito.when(userFeignClient.getAccess(anyString())).thenReturn(true);
        Mockito.when(userAppRepository.save(any(UserApp.class)))
                .thenReturn(new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()));

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(userAppDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Тест успешно проверяющий валидацию в контроллере")
    @SneakyThrows
    public void userCreateControllerValidTest() {

        //given
        UserAppDto userAppDto = new UserAppDto("Den", "testmail.ru");

        Mockito.when(userAppRepository.save(any(UserApp.class)))
                .thenReturn(new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()));

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(userAppDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Тест успешно проверяющий работу с внешним API в контроллере")
    @SneakyThrows
    public void userCreateControllerAccessTest() {

        //given
        UserAppDto userAppDto = new UserAppDto("Den", "test@mail.ru");

        Mockito.when(userFeignClient.getAccess(anyString())).thenReturn(false);
        Mockito.when(userAppRepository.save(any(UserApp.class)))
                .thenReturn(new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()));

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(userAppDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(mvcResult.getResolvedException()).isExactlyInstanceOf(AccessDeniedException.class);
        assertThat(Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()).isEqualTo("Access denied");
    }


    @ParameterizedTest
    @MethodSource("userProvider")
    @DisplayName("Тест успешно выполняющий взаимодействие контроллера с сервисов")
    @SneakyThrows
    public void userCreateControllerParametrizeMethodTest(UserAppDto userAppDto, UserApp userApp) {

        //given
        Mockito.when(userFeignClient.getAccess(anyString())).thenReturn(true);
        Mockito.when(userAppRepository.save(any(UserApp.class))).thenReturn(userApp);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(userAppDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    private static Stream<Arguments> userProvider() {
        return Stream.of(
                Arguments.of(
                        new UserAppDto("Den", "test@mail.ru"),
                        new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp())
                ),
                Arguments.of(
                        new UserAppDto("Alice", "testAlice@mail.ru"),
                        new UserApp(456L, "Alice", "testAlice@mail.ru", new AuthUserApp())
                )
        );
    }

    @ParameterizedTest
    @CsvSource({
            "123, Den, test@mail.ru, true",
            "456, Alice, testAlice@mail.ru, true"
    })
    @DisplayName("Тест успешно выполняющий взаимодействие контроллера с сервисов")
    @SneakyThrows
    public void userCreateControllerParametrizeCSVTest(long id, String name, String email, boolean isAccess) {

        //given
        UserAppDto userAppDto = new UserAppDto(name, email);

        Mockito.when(userFeignClient.getAccess(anyString())).thenReturn(isAccess);
        Mockito.when(userAppRepository.save(any(UserApp.class))).thenReturn(new UserApp(id, name, email, new AuthUserApp()));

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(userAppDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @EnumSource(EnumCase.class)
    @DisplayName("Тест успешно выполняющий взаимодействие контроллера с сервисов")
    @SneakyThrows
    public void userCreateControllerParametrizeEnumTest(EnumCase testCase) {

        //given
        Mockito.when(userFeignClient.getAccess(anyString())).thenReturn(testCase.getIsAccess());
        Mockito.when(userAppRepository.save(any(UserApp.class))).thenReturn(testCase.getUserApp());

        //when
        MvcResult mvcResult = mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(testCase.getUserAppDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andReturn();


        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(testCase.getStatusCode());
    }
}
