package core.basesyntax.bookstore.book;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.dto.book.BookDto;
import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.book.CreateBookRequestDto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final CreateBookRequestDto VALID_CREATE_REQUEST_DTO =
            new CreateBookRequestDto();
    private static final BookDto VALID_BOOK_DTO_1 = new BookDto();
    private static final BookDto VALID_BOOK_DTO_2 = new BookDto();
    private static final BookDto VALID_BOOK_DTO_3 = new BookDto();
    private static final BookDto VALID_BOOK_DTO_4 = new BookDto();
    private static final CreateBookRequestDto VALID_UPDATE_REQUEST_DTO =
            new CreateBookRequestDto();
    private static final BookDto VALID_UPDATE_DTO = new BookDto();

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
                    new ClassPathResource("database/book/add-default-book-and-categories.sql")
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
                    new ClassPathResource("database/book/delete-all-book.sql")
            );
        }
    }

    @BeforeEach
    public void setupTestData() {
        VALID_CREATE_REQUEST_DTO.setTitle("Book 3 Title");
        VALID_CREATE_REQUEST_DTO.setAuthor("Author 3");
        VALID_CREATE_REQUEST_DTO.setPrice(BigDecimal.valueOf(20));
        VALID_CREATE_REQUEST_DTO.setDescription("Description 3 for book 3");
        VALID_CREATE_REQUEST_DTO.setCategoryIds(Set.of(2L));
        VALID_CREATE_REQUEST_DTO.setIsbn("978-0307743657");
        VALID_CREATE_REQUEST_DTO.setCoverImage("http://example.com/book3.jpg");

        VALID_BOOK_DTO_1.setTitle(VALID_CREATE_REQUEST_DTO.getTitle());
        VALID_BOOK_DTO_1.setAuthor(VALID_CREATE_REQUEST_DTO.getAuthor());
        VALID_BOOK_DTO_1.setPrice(VALID_CREATE_REQUEST_DTO.getPrice());
        VALID_BOOK_DTO_1.setIsbn(VALID_CREATE_REQUEST_DTO.getIsbn());
        VALID_BOOK_DTO_1.setDescription(VALID_CREATE_REQUEST_DTO.getDescription());
        VALID_BOOK_DTO_1.setCoverImage(VALID_CREATE_REQUEST_DTO.getCoverImage());;
        VALID_BOOK_DTO_1.setCategories(VALID_CREATE_REQUEST_DTO.getCategoryIds());

        VALID_BOOK_DTO_2.setTitle(VALID_UPDATE_DTO.getTitle());
        VALID_BOOK_DTO_2.setAuthor(VALID_UPDATE_DTO.getAuthor());
        VALID_BOOK_DTO_2.setPrice(VALID_UPDATE_DTO.getPrice());
        VALID_BOOK_DTO_2.setIsbn(VALID_UPDATE_DTO.getIsbn());
        VALID_BOOK_DTO_2.setDescription(VALID_UPDATE_DTO.getDescription());
        VALID_BOOK_DTO_2.setCoverImage(VALID_UPDATE_DTO.getCoverImage());;
        VALID_BOOK_DTO_2.setCategories(VALID_UPDATE_DTO.getCategories());

        VALID_BOOK_DTO_3.setTitle("Book 2 Title");
        VALID_BOOK_DTO_3.setAuthor("Author 2");
        VALID_BOOK_DTO_3.setPrice(BigDecimal.valueOf(45));
        VALID_BOOK_DTO_3.setIsbn("978-0-316-03647-7");
        VALID_BOOK_DTO_3.setDescription("Description 2 for book 2");
        VALID_BOOK_DTO_3.setCoverImage("http://example.com/book2.jpg");;
        VALID_BOOK_DTO_3.setCategories(Set.of(1L));

        VALID_BOOK_DTO_4.setTitle("Book 3 Title");
        VALID_BOOK_DTO_4.setAuthor("Author 3");
        VALID_BOOK_DTO_4.setPrice(BigDecimal.valueOf(28));
        VALID_BOOK_DTO_4.setIsbn("978-0-316-03647-4");
        VALID_BOOK_DTO_4.setDescription("Description 3 for book 3");
        VALID_BOOK_DTO_4.setCoverImage("http://example.com/book3.jpg");;
        VALID_BOOK_DTO_4.setCategories(Set.of(2L));

        VALID_UPDATE_REQUEST_DTO.setTitle("updated Book 1 Title");
        VALID_UPDATE_REQUEST_DTO.setAuthor("Author 1");
        VALID_UPDATE_REQUEST_DTO.setPrice(BigDecimal.valueOf(20));
        VALID_UPDATE_REQUEST_DTO.setDescription("Description 1 for book 1");
        VALID_UPDATE_REQUEST_DTO.setIsbn("978-3-16-148410-0");
        VALID_UPDATE_REQUEST_DTO.setCategoryIds(Set.of(1L));
        VALID_UPDATE_REQUEST_DTO.setCoverImage("http://example.com/book1.jpg");

        VALID_UPDATE_DTO.setPrice(VALID_UPDATE_REQUEST_DTO.getPrice());
        VALID_UPDATE_DTO.setAuthor(VALID_UPDATE_REQUEST_DTO.getAuthor());
        VALID_UPDATE_DTO.setTitle(VALID_UPDATE_REQUEST_DTO.getTitle());
        VALID_UPDATE_DTO.setDescription(VALID_UPDATE_REQUEST_DTO.getDescription());
        VALID_UPDATE_DTO.setIsbn(VALID_UPDATE_REQUEST_DTO.getIsbn());
        VALID_UPDATE_DTO.setCoverImage(VALID_UPDATE_REQUEST_DTO.getCoverImage());
        VALID_UPDATE_DTO.setCategories(VALID_UPDATE_REQUEST_DTO.getCategoryIds());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Test createBook endpoint with valid request")
    public void createBook_validRequestDto_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_CREATE_REQUEST_DTO))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    BookDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            BookDto.class
                    );
                    Assertions.assertNotNull(actual);
                    boolean expression = org.apache.commons.lang3.builder.EqualsBuilder
                            .reflectionEquals(
                                    VALID_BOOK_DTO_1,
                                    actual,
                                    "id"
                            );
                    Assertions.assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test getAll endpoint")
    @WithMockUser(username = "admin")
    void getAll_returnResponse() throws Exception {
        mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<BookDto> actualList = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    BookDto.class));
                    Assertions.assertNotNull(actualList);
                    Assertions.assertEquals(3, actualList.size());
                    boolean expression3 = EqualsBuilder.reflectionEquals(
                            VALID_BOOK_DTO_4,
                            actualList.get(2),
                            "id"
                    );
                    Assertions.assertTrue(expression3);
                });
    }

    @Test
    @DisplayName("Test getBookById endpoint with valid ID")
    @WithMockUser(username = "admin")
    void getBookById_validId_returnResponse() throws Exception {
        mockMvc.perform(
                        get("/books/2")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    BookDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            BookDto.class
                    );
                    Assertions.assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_BOOK_DTO_3,
                            actual,
                            "id"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test searchBooks endpoint with valid parameters")
    @WithMockUser(username = "admin")
    void searchBooks_validParameters_returnResponse() throws Exception {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Book 2 Title"},
                new String[]{"Author 2"},
                10,
                50
        );
        mockMvc.perform(
                        get("/books/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("title", params.title())
                                .param("author", params.author())
                                .param("fromPrice", String.valueOf(params.fromPrice()))
                                .param("toPrice", String.valueOf(params.toPrice()))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<BookDto> actualList = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    BookDto.class));
                    Assertions.assertNotNull(actualList);
                    Assertions.assertEquals(1, actualList.size());
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_BOOK_DTO_3,
                            actualList.get(0),
                            "id"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test updateById endpoint with valid ID and request")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateById_validIdAndRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST_DTO))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    BookDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            BookDto.class
                    );
                    Assertions.assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_UPDATE_DTO,
                            actual,
                            "id"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @Test
    @DisplayName("Test delete endpoint with valid ID")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_validId_returnNoContent() throws Exception {
        mockMvc.perform(
                        delete("/books/3")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
