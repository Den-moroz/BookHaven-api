package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.book.BookDto;
import core.basesyntax.bookstore.dto.book.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.book.CreateBookRequestDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.BookMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.book.BookSpecificationBuilder;
import core.basesyntax.bookstore.service.BookService;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        List<Book> books = bookRepository.findAllWithCategories(pageable);
        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findByIdWithCategories(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found with id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto bookRequestDto) {
        bookRepository.findByIdWithCategories(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found with id: " + id)
        );
        Book updatedBook = bookMapper.toModel(bookRequestDto);
        updatedBook.setId(id);
        return bookMapper.toDto(bookRepository.save(updatedBook));
    }

    @Override
    public List<BookDto> findByParams(BookSearchParametersDto params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);

        List<Book> books = bookRepository.findAll((root, query, criteriaBuilder) -> {
            root.fetch("categories", JoinType.LEFT);
            query.distinct(true);
            return bookSpecification.toPredicate(root, query, criteriaBuilder);
        });

        return books.stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.findByIdWithCategories(id).orElseThrow(
                () -> new EntityNotFoundException("Book not found with id: " + id)
        );
        bookRepository.deleteById(id);
    }
}
