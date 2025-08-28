package oleborn.testresearch.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.SneakyThrows;
import oleborn.testresearch.model.dto.LoginDto;
import oleborn.testresearch.model.dto.RegisterDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.repository.AuthUserAppRepository;
import oleborn.testresearch.repository.UserAppRepository;
import oleborn.testresearch.security.JwtUtils;
import oleborn.testresearch.util.FileReader;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseTestContainersTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres");

    @AfterEach
    public void cleanUp() {
        userAppRepository.deleteAll();
        authUserAppRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;


    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private AuthUserAppRepository authUserAppRepository;

    @Autowired
    private FileReader fileReader;

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

        MvcResult mvcResult = mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(registerDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        Optional<UserApp> byMail = userAppRepository.findByMail(registerDto.mail());
        assertThat(byMail.isPresent()).isTrue();
        assertThat(byMail.get().getName()).isEqualTo(registerDto.name());

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();
    }

    @Test
    @SneakyThrows
    public void loginUserTestV1() {
        //given
        LoginDto loginDto = LoginDto.builder()
                .mail("test@mail.ru")
                .password("password")
                .build();

        AuthUserApp authUserApp = AuthUserApp.builder()
                .mail("test@mail.ru")
                .password(passwordEncoder.encode("password"))
                .roles(List.of("USER"))
                .build();

        authUserAppRepository.save(authUserApp);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();
    }

    @Test
    @SneakyThrows
    public void loginUserTestV2() {
        //given
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();

        LoginDto loginDto = Instancio.of(LoginDto.class)
                .set(field(LoginDto::mail), email)
                .set(field(LoginDto::password), password)
                .create();

        AuthUserApp authUserApp = Instancio.of(AuthUserApp.class)
                .set(field(AuthUserApp::getMail), email)
                .set(field(AuthUserApp::getPassword), passwordEncoder.encode(password))
                .ignore(field("id"))
                .ignore(field(AuthUserApp::getUserApp))
                .create();

        System.out.println(authUserApp);
        System.out.println(loginDto);

        authUserAppRepository.save(authUserApp);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();

    }

    @Test
    @SneakyThrows
    public void loginUserTestV3() {
        //given
        LoginDto loginDto = fileReader.readFile("json/LoginDtoInput.json", LoginDto.class);

        AuthUserApp authUserApp = fileReader.readFile("json/AuthUserAppInput.json", AuthUserApp.class);

        authUserAppRepository.save(authUserApp);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();

    }

    @Test
    @SneakyThrows
    @Sql(scripts = {
            "/sql/insert_user_app.sql",
            "/sql/insert_auth_user.sql",
            "/sql/insert_user_roles.sql"
    })
    public void loginUserTestV4() {
        //given
        LoginDto loginDto = fileReader.readFile("json/LoginDtoInput.json", LoginDto.class);

        //when
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .content(objectMapper.writeValueAsString(loginDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        //then
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();
    }

    @Test
    @SneakyThrows
    public void testTrigger() {
        RegisterDto registerDto = RegisterDto.builder()
                .name("Den")
                .mail("TEST@MAIL.RU")
                .password("password")
                .build();

        stubFor(get(urlPathEqualTo("/users-id/" + URLEncoder.encode(registerDto.mail(), StandardCharsets.UTF_8)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")
                )
        );

        MvcResult mvcResult = mockMvc.perform(post("/auth/register")
                .content(objectMapper.writeValueAsString(registerDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andReturn();

        Optional<UserApp> byMail = userAppRepository.findByMail(registerDto.mail());
        assertThat(byMail.isPresent()).isTrue();
        assertThat(byMail.get().getName()).isEqualTo(registerDto.name());

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);

        boolean resultToken = jwtUtils.validateToken(mvcResult.getResponse().getContentAsString());

        assertThat(resultToken).isTrue();

        List<AuthUserApp> all = authUserAppRepository.findAll();
        System.out.println(all.getFirst().getMail());
    }
}
