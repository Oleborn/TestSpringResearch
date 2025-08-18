package oleborn.testresearch.moduletest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import oleborn.testresearch.model.dto.LoginDto;
import oleborn.testresearch.model.dto.RegisterDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.repository.AuthUserAppRepository;
import oleborn.testresearch.repository.UserAppRepository;
import oleborn.testresearch.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@AutoConfigureWireMock(port = 0)
public class AuthUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @MockitoBean
    private AuthUserAppRepository authUserAppRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @SneakyThrows
    public void registerUserTest() {
        RegisterDto registerDto = RegisterDto.builder()
                .name("Den")
                .mail("test@mail.ru")
                .password("password")
                .build();

        stubFor(get(urlPathEqualTo("/users-id/" + URLEncoder.encode(registerDto.mail(), StandardCharsets.UTF_8)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                )
        );

        UserApp userApp = new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp());
        AuthUserApp authUserApp = AuthUserApp.builder()
                .id(124L)
                .mail("test@mail.ru")
                .password(passwordEncoder.encode("password"))
                .roles(List.of("USER"))
                .build();

        Mockito.when(userAppRepository.save(any(UserApp.class))).thenReturn(userApp);
        Mockito.when(authUserAppRepository.save(any(AuthUserApp.class))).thenReturn(authUserApp);

        MvcResult mvcResult = mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(registerDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();
    }

    @Test
    @SneakyThrows
    public void loginUserTest() {
        LoginDto loginDto = LoginDto.builder()
                .mail("test@mail.ru")
                .password("password")
                .build();

        AuthUserApp authUserApp = AuthUserApp.builder()
                .id(124L)
                .mail("test@mail.ru")
                .password(passwordEncoder.encode("password"))
                .roles(List.of("USER"))
                .build();

        Mockito.when(authUserAppRepository.findByMailAndPassword(anyString(), anyString())).thenReturn(Optional.ofNullable(authUserApp));

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();
    }

    @Test
    @SneakyThrows
    public void getUserTest() {

        Cache cache = cacheManager.getCache("users");

        UserApp userApp = new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp());

        String token = jwtUtils.generateToken(userApp.getMail(), List.of("ADMIN"));
        Mockito.when(userAppRepository.findByMail(anyString())).thenReturn(Optional.of(userApp));

        MvcResult mvcResultOne = mockMvc.perform(MockMvcRequestBuilders.get("/users/get")
                .param("mail", userApp.getMail())
                .header("Authorization", "Bearer " + token)
        ).andReturn();

        assertThat(mvcResultOne.getResponse().getStatus()).isEqualTo(200);

        Mockito.verify(userAppRepository, Mockito.times(1)).findByMail(userApp.getMail());

        MvcResult mvcResultTwo = mockMvc.perform(MockMvcRequestBuilders.get("/users/get")
                .param("mail", userApp.getMail())
                .header("Authorization", "Bearer " + token)
        ).andReturn();

        Mockito.verify(userAppRepository, Mockito.times(1)).findByMail(userApp.getMail());

        cache.clear();

        MvcResult mvcResultThree = mockMvc.perform(MockMvcRequestBuilders.get("/users/get")
                .param("mail", userApp.getMail())
                .header("Authorization", "Bearer " + token)
        ).andReturn();

        Mockito.verify(userAppRepository, Mockito.times(2)).findByMail(userApp.getMail());

        assertThat(mvcResultOne.getResponse().getContentAsString()).isEqualTo(mvcResultThree.getResponse().getContentAsString());

    }
}
