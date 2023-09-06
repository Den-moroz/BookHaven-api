package core.basesyntax.bookstore.category;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import core.basesyntax.bookstore.controller.CategoryController;
import core.basesyntax.bookstore.dto.book.BookDtoWithoutCategoryIds;
import core.basesyntax.bookstore.dto.category.CategoryDto;
import core.basesyntax.bookstore.dto.category.CreateCategoryRequestDto;
import core.basesyntax.bookstore.mapper.BookMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Category;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.category.CategoryRepository;
import core.basesyntax.bookstore.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {

    private static final CreateCategoryRequestDto VALID_CREATE_REQUEST_DTO = new CreateCategoryRequestDto();
    private static final CategoryDto VALID_CATEGORY_DTO = new CategoryDto();
    private static final Category VALID_CATEGORY = new Category();
    private static final List<CategoryDto> VALID_CATEGORY_LIST = Collections.singletonList(new CategoryDto());
    private static final Long VALID_CATEGORY_ID = 1L;

    protected static MockMvc mockMvc;

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

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
                    new ClassPathResource("database/category/add-categories.sql")
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
                    new ClassPathResource("database/category/remove-all-category.sql")
            );
        }
    }

    @BeforeEach
    public void setUp() {
        VALID_CREATE_REQUEST_DTO.setName("Category Name");
        VALID_CREATE_REQUEST_DTO.setDescription("Category Description");

        VALID_CATEGORY_DTO.setId(VALID_CATEGORY_ID);
        VALID_CATEGORY_DTO.setName("Category Name");
        VALID_CATEGORY_DTO.setDescription("Category Description");

        VALID_CATEGORY.setId(VALID_CATEGORY_ID);
        VALID_CATEGORY.setName("Category Name");
        VALID_CATEGORY.setDescription("Category Description");
    }

    @Test
    @DisplayName("Create category with valid request DTO should return the category")
    public void createCategory_validRequestDto_returnResponse() throws Exception {
        when(categoryService.save(any(CreateCategoryRequestDto.class))).thenReturn(VALID_CATEGORY_DTO);

        mockMvc.perform(
                post("/categories")
                        .content(asJsonString(VALID_CREATE_REQUEST_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value("Category Name"))
                .andExpect(jsonPath("$.description").value("Category Description"));
        verify(categoryService, times(1)).save(any(CreateCategoryRequestDto.class));
    }

    @Test
    @DisplayName("Get all categories should return a list of categories")
    public void getAllCategories_returnCategories() throws Exception {
        when(categoryService.findAll(any(Pageable.class))).thenReturn(VALID_CATEGORY_LIST);

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(VALID_CATEGORY_ID))
                .andExpect(jsonPath("$[0].name").value("Category Name"))
                .andExpect(jsonPath("$[0].description").value("Category Description"));
        verify(categoryService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Get category by valid ID should return the category")
    public void getCategoryById_validId_returnCategory() throws Exception {
        when(categoryService.getById(VALID_CATEGORY_ID)).thenReturn(VALID_CATEGORY_DTO);

        mockMvc.perform(get("/categories/{id}", VALID_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value("Category Name"))
                .andExpect(jsonPath("$.description").value("Category Description"));
        verify(categoryService, times(1)).getById(VALID_CATEGORY_ID);
    }

    @Test
    @DisplayName("Update category with valid ID and request DTO should return the updated category")
    public void updateCategory_validIdAndDto_returnUpdatedCategory() throws Exception {
        when(categoryService.update(VALID_CATEGORY_ID, VALID_CREATE_REQUEST_DTO)).thenReturn(VALID_CATEGORY_DTO);

        mockMvc.perform(
                        put("/categories/{id}", VALID_CATEGORY_ID)
                                .content(asJsonString(VALID_CREATE_REQUEST_DTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(VALID_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value("Category Name"))
                .andExpect(jsonPath("$.description").value("Category Description"));
        verify(categoryService, times(1)).update(VALID_CATEGORY_ID, VALID_CREATE_REQUEST_DTO);
    }

    @Test
    @DisplayName("Delete category by valid ID should return no content")
    public void deleteCategory_validId_returnNoContent() throws Exception {
        doNothing().when(categoryService).deleteById(VALID_CATEGORY_ID);

        mockMvc.perform(
                        delete("/categories/{id}", VALID_CATEGORY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(categoryService, times(1)).deleteById(VALID_CATEGORY_ID);
    }

    @Test
    @DisplayName("Get books by category ID should return a list of books")
    public void getBooksByCategoryId_validId_returnListOfBooks() throws Exception {
        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(VALID_CATEGORY));
        when(bookRepository.findAllByCategoriesIdWithCategories(VALID_CATEGORY_ID)).thenReturn(Collections.singletonList(new Book()));
        when(bookMapper.toDtoWithoutCategories(any(Book.class))).thenReturn(new BookDtoWithoutCategoryIds());

        mockMvc.perform(
                        get("/categories/{id}/books", VALID_CATEGORY_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(categoryRepository, times(1)).findById(VALID_CATEGORY_ID);
        verify(bookRepository, times(1)).findAllByCategoriesIdWithCategories(VALID_CATEGORY_ID);
        verify(bookMapper, times(1)).toDtoWithoutCategories(any(Book.class));
    }

    private String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}