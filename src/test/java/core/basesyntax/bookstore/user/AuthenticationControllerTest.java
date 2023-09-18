package core.basesyntax.bookstore.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.dto.user.UserLoginRequestDto;
import core.basesyntax.bookstore.dto.user.UserLoginResponseDto;
import core.basesyntax.bookstore.dto.user.UserRegistrationRequestDto;
import core.basesyntax.bookstore.dto.user.UserRegistrationResponseDto;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    private static final UserRegistrationRequestDto VALID_REGISTRATION_REQUEST =
            new UserRegistrationRequestDto();
    private static final UserRegistrationResponseDto VALID_REGISTRATION_RESPONSE =
            new UserRegistrationResponseDto();
    private static final UserRegistrationRequestDto INVALID_REGISTRATION_REQUEST =
            new UserRegistrationRequestDto();
    private static final UserLoginRequestDto VALID_LOGIN_REQUEST =
            new UserLoginRequestDto("denis@i.ua", "zxcvbnmas");
    private static final UserLoginRequestDto NON_EXISTING_USER =
            new UserLoginRequestDto("uknown@i.ua", "qwertyuiop");

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VALID_REGISTRATION_REQUEST.setEmail("admin@i.ua");
        VALID_REGISTRATION_REQUEST.setPassword("zxcvbnmas");
        VALID_REGISTRATION_REQUEST.setRepeatPassword("zxcvbnmas");
        VALID_REGISTRATION_REQUEST.setFirstName("Denis");
        VALID_REGISTRATION_REQUEST.setLastName("Unknown");
        VALID_REGISTRATION_REQUEST.setShippingAddress("123, Kyiv");

        VALID_REGISTRATION_RESPONSE.setEmail(VALID_REGISTRATION_REQUEST.getEmail());
        VALID_REGISTRATION_RESPONSE.setFirstName(VALID_REGISTRATION_REQUEST.getFirstName());
        VALID_REGISTRATION_RESPONSE.setLastName(VALID_REGISTRATION_REQUEST.getLastName());
        VALID_REGISTRATION_RESPONSE.setLastName(VALID_REGISTRATION_REQUEST.getLastName());
        VALID_REGISTRATION_RESPONSE.setShippingAddress(
                VALID_REGISTRATION_REQUEST.getShippingAddress());

        INVALID_REGISTRATION_REQUEST.setEmail("email@i.ua");
        INVALID_REGISTRATION_REQUEST.setPassword("asdfghjkl");
        INVALID_REGISTRATION_REQUEST.setRepeatPassword("asdfghjk");
        INVALID_REGISTRATION_REQUEST.setFirstName("Denis");
        INVALID_REGISTRATION_REQUEST.setLastName("Unknown");
        INVALID_REGISTRATION_REQUEST.setShippingAddress("123, Kyiv");
    }

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/user/add-default-role.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-db.sql")
            );
        }
    }

    @Test
    @DisplayName("Test register endpoint with valid request")
    public void register_validRequestDto_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REGISTRATION_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    UserRegistrationResponseDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            UserRegistrationResponseDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_REGISTRATION_RESPONSE,
                            actual,
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test register endpoint with invalid request")
    public void register_invalidRequestDto_returnValidationError() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_REGISTRATION_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    JsonNode responseNode = objectMapper.readTree(responseContent);
                    JsonNode errorsNode = responseNode.get("errors");

                    assertNotNull(errorsNode);
                    assertTrue(errorsNode.isArray());

                    JsonNode firstError = errorsNode.get(0);
                    assertEquals("Passwords do not match", firstError.textValue());
                });
    }

    @Test
    @DisplayName("Test login endpoint with valid request")
    @Sql(scripts = "classpath:database/user/add-user.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void login_validRequestDto_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_LOGIN_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    UserLoginResponseDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            UserLoginResponseDto.class
                    );
                    assertNotNull(actual);
                });
    }

    @Test
    @DisplayName("Test login endpoint with non-existing user")
    public void login_nonExistingUser_throwError() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NON_EXISTING_USER))
        )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
