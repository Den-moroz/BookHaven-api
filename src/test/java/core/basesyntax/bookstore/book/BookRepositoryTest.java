package core.basesyntax.bookstore.book;

import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    @Sql(scripts = {
            "classpath:database/category/add-categories.sql",
            "classpath:database/book/add-two-book.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByCategoriesId_validCategoriesId_returnOneBook() {
        List<Book> actual = bookRepository.findAllByCategoriesIdWithCategories(2L);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("Book 1", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    void findAllByCategoriesId_validCategoriesId_returnTwoBook() {
        List<Book> actual = bookRepository.findAllByCategoriesIdWithCategories(1L);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("Book1", actual.get(0).getTitle());
        Assertions.assertEquals("Book2", actual.get(1).getTitle());
    }

    @Test
    @DisplayName("""
            Find all books with categories
            """)
    void findAllWithCategories_validTwoBook_returnTwoBookWithPagination() {
        List<Book> actual = bookRepository.findAllWithCategories(Mockito.any(Pageable.class));
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("Author 1", actual.get(0).getAuthor());
        Assertions.assertEquals("Author 2", actual.get(1).getAuthor());
    }

    @Test
    @DisplayName("""
            Find all books with categories by params
            """)
    void findAllByParamsWithCategories_validTwoBook_returnOneBook() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Book 1"}, new String[]{"Author 1"}, 10, 30);
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        List<Book> actual = bookRepository.findAllWithCategories(bookSpecification);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("cover1.jpg", actual.get(0).getCoverImage());
    }

    @Test
    @DisplayName("""
            Find book by id with categories
            """)
    void findByIdWithCategories_validTwoBook_returnOneBook() {
        Optional<Book> actual = bookRepository.findByIdWithCategories(1L);
        Assertions.assertEquals("Book 1", actual.get().getTitle());
    }
}