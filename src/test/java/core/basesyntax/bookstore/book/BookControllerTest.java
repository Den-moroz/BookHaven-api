package core.basesyntax.bookstore.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.controller.BookController;
import core.basesyntax.bookstore.dto.book.BookDto;
import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.book.CreateBookRequestDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.service.BookService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    private static final CreateBookRequestDto VALID_CREATE_REQUEST_DTO = new CreateBookRequestDto();
    private static final CreateBookRequestDto INVALID_CREATE_REQUEST_DTO = new CreateBookRequestDto();
    private static final BookDto VALID_BOOK_DTO = new BookDto();
    private static final BookDto INVALID_BOOK_DTO = new BookDto();
    private static final List<BookDto> VALID_BOOK_LIST = Collections.singletonList(new BookDto());
    private static final Long VALID_BOOK_ID = 1L;
    private static final Long INVALID_BOOK_ID = 100L;

    protected static MockMvc mockMvc;
    @Mock
    private BookService bookService;

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
                    new ClassPathResource("database/book/add-two-book.sql")
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
                    new ClassPathResource("database/book/remove-all-book.sql")
            );
        }
    }

    @WithMockUser
    @BeforeEach
    public void setupTestData() {
        VALID_CREATE_REQUEST_DTO.setTitle("Book Title");
        VALID_CREATE_REQUEST_DTO.setAuthor("Author");
        VALID_CREATE_REQUEST_DTO.setPrice(BigDecimal.valueOf(19.99));

        VALID_BOOK_DTO.setTitle("Book Title");
        VALID_BOOK_DTO.setAuthor("Author");
        VALID_BOOK_DTO.setPrice(BigDecimal.valueOf(19.99));

        INVALID_CREATE_REQUEST_DTO.setTitle("Invalid Title");
        INVALID_CREATE_REQUEST_DTO.setAuthor("Invalid Author");
        INVALID_CREATE_REQUEST_DTO.setPrice(BigDecimal.valueOf(29.99));

        INVALID_BOOK_DTO.setTitle("Invalid Title");
        INVALID_BOOK_DTO.setAuthor("Invalid Author");
        INVALID_BOOK_DTO.setPrice(BigDecimal.valueOf(29.99));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Test createBook endpoint with valid request")
    public void createBook_validRequestDto_returnResponse() throws Exception {
        when(bookService.save(VALID_CREATE_REQUEST_DTO)).thenReturn(VALID_BOOK_DTO);

        mockMvc.perform(
                post("/books")
                        .content(asJsonString(VALID_CREATE_REQUEST_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    @DisplayName("Test getAll endpoint")
    public void getAll_givenValidBook_returnBooks() throws Exception {
        when(bookService.findAll(any(Pageable.class))).thenReturn(VALID_BOOK_LIST);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("Test getBookById endpoint with valid ID")
    public void getBookById_validId_returnBook() throws Exception {
        when(bookService.getBookById(VALID_BOOK_ID)).thenReturn(VALID_BOOK_DTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{id}", VALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_BOOK_ID));
    }

    @Test
    @DisplayName("Test getBookById endpoint with invalid ID")
    public void getBookById_invalidId_throwException() throws Exception {
        when(bookService.getBookById(INVALID_BOOK_ID))
                .thenThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/{id}", INVALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Book not found with id: " + INVALID_BOOK_ID));
    }

    @Test
    @DisplayName("Test updateById endpoint with valid ID")
    public void updateById_validId() throws Exception {
        when(bookService.updateById(VALID_BOOK_ID, VALID_CREATE_REQUEST_DTO)).thenReturn(VALID_BOOK_DTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/books/{id}", VALID_BOOK_ID)
                        .content(asJsonString(VALID_CREATE_REQUEST_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_BOOK_ID))
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    @DisplayName("Test updateById endpoint with invalid ID")
    public void updateById_invalidId() throws Exception {
        when(bookService.updateById(INVALID_BOOK_ID, INVALID_CREATE_REQUEST_DTO))
                .thenThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/books/{id}", INVALID_BOOK_ID)
                        .content(asJsonString(INVALID_CREATE_REQUEST_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Book not found with id: " + INVALID_BOOK_ID));
    }

    @Test
    @DisplayName("Test searchBooks endpoint")
    public void searchBooks() throws Exception {
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                new String[]{"Title 1", "Title 2"},
                new String[]{"Author 1", "Author 2"},
                10,
                50
        );

        when(bookService.findByParams(searchParameters)).thenReturn(VALID_BOOK_LIST);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/books/search")
                        .content(asJsonString(searchParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("Test delete endpoint with valid ID")
    public void delete_validId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/books/{id}", VALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test delete endpoint with invalid ID")
    public void delete_invalidId() throws Exception {
        doThrow(new EntityNotFoundException("Book not found with id: " + INVALID_BOOK_ID))
                .when(bookService).deleteById(INVALID_BOOK_ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/books/{id}", INVALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Book not found with id: " + INVALID_BOOK_ID));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}