package core.basesyntax.bookstore.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.dto.book.BookDto;
import core.basesyntax.bookstore.dto.book.BookDtoWithoutCategoryIds;
import core.basesyntax.bookstore.dto.category.CategoryDto;
import core.basesyntax.bookstore.dto.category.CreateCategoryRequestDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final CreateCategoryRequestDto VALID_CREATE_REQUEST_DTO =
            new CreateCategoryRequestDto();
    private static final CategoryDto VALID_CATEGORY_DTO = new CategoryDto();
    private static final CategoryDto VALID_GET_RESPONSE = new CategoryDto();
    private static final BookDtoWithoutCategoryIds VALID_BOOK_WITHOUT_CATEGORY =
            new BookDtoWithoutCategoryIds();
    private static final CreateCategoryRequestDto VALID_UPDATE_REQUEST_DTO =
            new CreateCategoryRequestDto();
    private static final CategoryDto VALID_UPDATE_DTO = new CategoryDto();

    @Autowired
    private ObjectMapper objectMapper;

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
                    new ClassPathResource("database/category/add-two-default-category.sql")
            );
        }
    }

    @BeforeEach
    public void setupTestData() {
        VALID_CREATE_REQUEST_DTO.setName("New Category");
        VALID_CREATE_REQUEST_DTO.setDescription("Category Description");

        VALID_CATEGORY_DTO.setName(VALID_CREATE_REQUEST_DTO.getName());
        VALID_CATEGORY_DTO.setDescription(VALID_CREATE_REQUEST_DTO.getDescription());

        VALID_GET_RESPONSE.setName("Category 2");
        VALID_GET_RESPONSE.setDescription("Description 2");

        VALID_BOOK_WITHOUT_CATEGORY.setTitle("Title 1");
        VALID_BOOK_WITHOUT_CATEGORY.setAuthor("Author 1");
        VALID_BOOK_WITHOUT_CATEGORY.setPrice(BigDecimal.valueOf(20));
        VALID_BOOK_WITHOUT_CATEGORY.setIsbn("ISBN 1");
        VALID_BOOK_WITHOUT_CATEGORY.setDescription("Description 1");
        VALID_BOOK_WITHOUT_CATEGORY.setCoverImage("Cover image 1");

        VALID_UPDATE_REQUEST_DTO.setName("Updated title");
        VALID_UPDATE_REQUEST_DTO.setDescription("Updated description");

        VALID_UPDATE_DTO.setName(VALID_UPDATE_REQUEST_DTO.getName());
        VALID_UPDATE_DTO.setDescription(VALID_UPDATE_REQUEST_DTO.getDescription());
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Test createCategory endpoint with valid request")
    public void createCategory_validRequestDto_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_CREATE_REQUEST_DTO))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    CategoryDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            CategoryDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = org.apache.commons.lang3.builder.EqualsBuilder
                            .reflectionEquals(
                                    VALID_CATEGORY_DTO,
                                    actual,
                                    "id"
                            );
                    assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test getAll endpoint for category")
    @WithMockUser(username = "admin")
    void getAll_validThreeCategory_returnResponse() throws Exception {
        mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<BookDto> actualList = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    CategoryDto.class));
                    assertNotNull(actualList);
                    assertEquals(4, actualList.size());
                    boolean expression3 = EqualsBuilder.reflectionEquals(
                            VALID_GET_RESPONSE,
                            actualList.get(1),
                            "id"
                    );
                    assertTrue(expression3);
                });
    }

    @Test
    @DisplayName("Test getCategoryById endpoint with valid ID")
    @WithMockUser(username = "admin")
    void getCategoryById_validId_returnResponse() throws Exception {
        mockMvc.perform(
                        get("/categories/2")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    CategoryDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            CategoryDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_GET_RESPONSE,
                            actual,
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test getBookByCategoryId endpoint with valid ID")
    @WithMockUser(username = "admin")
    void getBookByCategoryId_validCategoryId_returnResponse() throws Exception {
        mockMvc.perform(
                        get("/categories/1/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<BookDtoWithoutCategoryIds> actualList = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    BookDtoWithoutCategoryIds.class));
                    assertNotNull(actualList);
                    assertEquals(1, actualList.size());
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_BOOK_WITHOUT_CATEGORY,
                            actualList.get(0),
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test getCategoryById endpoint with invalid ID")
    @WithMockUser(username = "admin")
    void getCategoryById_invalidId_returnNotFound() throws Exception {
        mockMvc.perform(
                        get("/categories/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Test updateById endpoint with valid ID and request")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateById_validIdAndRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST_DTO))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    CategoryDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            CategoryDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_UPDATE_DTO,
                            actual,
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test updateById endpoint with invalid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateById_invalidId_returnNotFound() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/categories/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST_DTO))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Test delete endpoint with valid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_validId_returnNoContent() throws Exception {
        mockMvc.perform(
                        delete("/categories/3")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Test delete endpoint with invalid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_invalidId_returnNotFound() throws Exception {
        mockMvc.perform(
                        delete("/categories/-1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
