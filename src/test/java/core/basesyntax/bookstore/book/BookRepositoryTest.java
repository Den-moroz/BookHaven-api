package core.basesyntax.bookstore.book;

import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Category;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.book.BookSpecificationBuilder;
import core.basesyntax.bookstore.repository.category.CategoryRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@ComponentScan("core.basesyntax.bookstore.repository.book")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    private static final Category VALID_CATEGORY_1 = new Category();
    private static final Category VALID_CATEGORY_2 = new Category();
    private static final Book VALID_BOOK_1 = new Book();
    private static final Book VALID_BOOK_2 = new Book();

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BookSpecificationBuilder bookSpecificationBuilder;

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

    @BeforeEach
    void setUp() {
        VALID_CATEGORY_1.setName("Category 1");
        VALID_CATEGORY_1.setDescription("Description 1");

        VALID_CATEGORY_2.setName("Category 2");
        VALID_CATEGORY_2.setDescription("Description 2");

        VALID_BOOK_1.setTitle("Book 1");
        VALID_BOOK_1.setAuthor("Author 1");
        VALID_BOOK_1.setPrice(BigDecimal.valueOf(15));
        VALID_BOOK_1.setIsbn("ISBN 1");
        VALID_BOOK_1.setDescription("Description 1");
        VALID_BOOK_1.setCoverImage("Cover image 1");

        VALID_BOOK_2.setTitle("Book 2");
        VALID_BOOK_2.setAuthor("Author 2");
        VALID_BOOK_2.setPrice(BigDecimal.valueOf(10));
        VALID_BOOK_2.setIsbn("ISBN 2");
        VALID_BOOK_2.setDescription("Description 2");
        VALID_BOOK_2.setCoverImage("Cover image 2");
    }

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    void findAllByCategoriesId_validCategoriesId_returnOneBook() {
        List<Book> savedBook = savedBooks();
        List<Long> categoryIds = savedBook
                .stream()
                .flatMap(book -> book.getCategories().stream())
                .map(Category::getId)
                .toList();

        List<Book> actual = bookRepository.findAllByCategoriesIdWithCategories(categoryIds.get(0));
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(VALID_BOOK_1.getTitle(), actual.get(0).getTitle());
    }

    @Test
    @DisplayName("""
            Find all books with categories
            """)
    void findAllWithCategories_validTwoBook_returnTwoBookWithPagination() {
        savedBooks();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllWithCategories(pageable);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(VALID_BOOK_1.getAuthor(), actual.get(0).getAuthor());
        Assertions.assertEquals(VALID_BOOK_2.getAuthor(), actual.get(1).getAuthor());
    }

    @Test
    @DisplayName("""
            Find all books with categories by params
            """)
    void findAllByParamsWithCategories_validTwoBook_returnOneBook() {
        savedBooks();
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Book 1"}, new String[]{"Author 1"}, 10, 30);
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        List<Book> actual = bookRepository.findAll(bookSpecification);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(VALID_BOOK_1.getCoverImage(), actual.get(0).getCoverImage());
    }

    @Test
    @DisplayName("""
            Find book by id with categories
            """)
    void findByIdWithCategories_validTwoBook_returnBook() {
        List<Book> savedBooks = savedBooks();
        Optional<Book> actual = bookRepository.findByIdWithCategories(savedBooks.get(0).getId());
        Assertions.assertEquals(VALID_BOOK_1.getTitle(), actual.get().getTitle());
    }

    @Test
    @DisplayName("Find all books by invalid category id")
    void findAllByCategoriesId_invalidCategoryId_returnEmptyList() {
        List<Long> invalidCategoryIds = List.of(-1L, -2L);
        List<Book> actual = bookRepository
                .findAllByCategoriesIdWithCategories(invalidCategoryIds.get(0));
        Assertions.assertTrue(actual.isEmpty());
    }

    private List<Category> saveCategory() {
        Category savedCategory1 = categoryRepository.save(VALID_CATEGORY_1);
        Category savedCategory2 = categoryRepository.save(VALID_CATEGORY_2);
        return List.of(savedCategory1, savedCategory2);
    }

    private List<Book> savedBooks() {
        List<Category> savedCategory = saveCategory();
        VALID_BOOK_1.setCategories(Set.of(savedCategory.get(0)));
        VALID_BOOK_2.setCategories(Set.of(savedCategory.get(1)));
        Book savedBook1 = bookRepository.save(VALID_BOOK_1);
        Book savedBook2 = bookRepository.save(VALID_BOOK_2);
        return List.of(savedBook1, savedBook2);
    }
}
