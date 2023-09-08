package core.basesyntax.bookstore.book;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.book.BookDto;
import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.book.CreateBookRequestDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.BookMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Category;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.book.BookSpecificationBuilder;
import core.basesyntax.bookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final CreateBookRequestDto REQUEST_DTO = new CreateBookRequestDto();
    private static final Book VALID_BOOK = new Book();
    private static final Category VALID_CATEGORY = new Category();
    private static final BookDto VALID_RESPONSE = new BookDto();
    private static final BookSearchParametersDto REQUEST_PARAMS = new BookSearchParametersDto(
            new String[]{"Title 1", "Title 2"},
            new String[]{"Author 1", "Author 2"},
            10,
            50
    );
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @BeforeEach
    void setUp() {
        REQUEST_DTO.setTitle("Book 1");
        REQUEST_DTO.setAuthor("Author 1");
        REQUEST_DTO.setPrice(BigDecimal.ONE);
        REQUEST_DTO.setDescription("Description 1");
        REQUEST_DTO.setCategoryIds(Set.of(1L));
        REQUEST_DTO.setIsbn("978-0-13-516630-7");

        VALID_CATEGORY.setId(1L);
        VALID_CATEGORY.setName("Category 1");
        VALID_CATEGORY.setDescription("Description 1");

        VALID_BOOK.setId(1L);
        VALID_BOOK.setCategories(Set.of(VALID_CATEGORY));
        VALID_BOOK.setPrice(REQUEST_DTO.getPrice());
        VALID_BOOK.setIsbn(REQUEST_DTO.getIsbn());
        VALID_BOOK.setAuthor(REQUEST_DTO.getAuthor());
        VALID_BOOK.setTitle(REQUEST_DTO.getTitle());
        VALID_BOOK.setDescription(REQUEST_DTO.getDescription());
        VALID_BOOK.setCoverImage(REQUEST_DTO.getCoverImage());

        VALID_RESPONSE.setId(VALID_BOOK.getId());
        VALID_RESPONSE.setTitle(VALID_BOOK.getTitle());
        VALID_RESPONSE.setAuthor(VALID_BOOK.getAuthor());
        VALID_RESPONSE.setPrice(VALID_BOOK.getPrice());
        VALID_RESPONSE.setIsbn(VALID_BOOK.getIsbn());
        VALID_RESPONSE.setDescription(VALID_BOOK.getDescription());
        VALID_RESPONSE.setCoverImage(VALID_BOOK.getCoverImage());
        Set<Long> categoriesId = VALID_BOOK.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        VALID_RESPONSE.setCategories(categoriesId);
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_validCreateBookRequestDto_returnBookDto() {
        when(bookMapper.toModel(REQUEST_DTO)).thenReturn(VALID_BOOK);
        when(bookRepository.save(VALID_BOOK)).thenReturn(VALID_BOOK);
        when(bookMapper.toDto(VALID_BOOK)).thenReturn(VALID_RESPONSE);

        BookDto savedBookDto = bookService.save(REQUEST_DTO);
        Assertions.assertEquals(VALID_RESPONSE, savedBookDto);
        verify(bookRepository, times(1)).save(VALID_BOOK);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_includedPagination_returnListBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAllWithCategories(pageable))
                .thenReturn(Collections.singletonList(VALID_BOOK));
        when(bookMapper.toDto(VALID_BOOK)).thenReturn(VALID_RESPONSE);

        List<BookDto> actual = bookService.findAll(pageable);
        verify(bookRepository).findAllWithCategories(pageable);
        verify(bookMapper).toDto(VALID_BOOK);
        Assertions.assertEquals(Collections.singletonList(VALID_RESPONSE), actual);
    }

    @Test
    @DisplayName("Test getBookById with existing book")
    void findById_validId_returnBook() {
        when(bookRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(VALID_BOOK));
        when(bookMapper.toDto(VALID_BOOK)).thenReturn(VALID_RESPONSE);

        BookDto actual = bookService.getBookById(1L);
        verify(bookRepository).findById(1L);
        verify(bookMapper).toDto(VALID_BOOK);
        Assertions.assertEquals(VALID_RESPONSE, actual);
    }

    @Test
    @DisplayName("Test getBookById with non-existing book")
    void findById_invalidId_throwException() {
        when(bookRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(100L));
        verify(bookRepository).findById(100L);
        Assertions.assertEquals("Book not found with id: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Test updateById with existing book")
    void updateById_validRequest_returnResponse() {
        when(bookMapper.toModel(REQUEST_DTO)).thenReturn(VALID_BOOK);
        when(bookRepository.save(VALID_BOOK)).thenReturn(VALID_BOOK);
        when(bookRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(VALID_BOOK));
        when(bookMapper.toDto(VALID_BOOK)).thenReturn(VALID_RESPONSE);

        BookDto actual = bookService.updateById(1L, REQUEST_DTO);
        Assertions.assertEquals(VALID_RESPONSE, actual);
        verify(bookMapper).toModel(REQUEST_DTO);
        verify(bookRepository).save(VALID_BOOK);
        verify(bookMapper).toDto(VALID_BOOK);
    }

    @Test
    @DisplayName("Test findByParams with valid parameters")
    void findByParams_validParameters_returnBook() {
        Specification<Book> bookSpecification = Mockito.mock(Specification.class);
        when(bookSpecificationBuilder.build(REQUEST_PARAMS)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification))
                .thenReturn(Collections.singletonList(VALID_BOOK));
        when(bookMapper.toDto(VALID_BOOK)).thenReturn(VALID_RESPONSE);

        List<BookDto> result = bookService.findByParams(REQUEST_PARAMS);
        Assertions.assertEquals(Collections.singletonList(VALID_RESPONSE), result);
        verify(bookSpecificationBuilder).build(REQUEST_PARAMS);
        verify(bookRepository).findAll(bookSpecification);
        verify(bookMapper).toDto(VALID_BOOK);
    }

    @Test
    @DisplayName("Test deleteById with valid book ID")
    void deleteById_validBookId_successfullyDeleted() {
        Long validBookId = 1L;
        Assertions.assertDoesNotThrow(() -> bookService.deleteById(validBookId));
        verify(bookRepository).deleteById(validBookId);
    }
}
