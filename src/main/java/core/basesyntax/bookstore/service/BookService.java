package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.BookDto;
import core.basesyntax.bookstore.dto.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto bookRequestDto);

    List<BookDto> findByParams(BookSearchParametersDto params);

    void deleteById(Long id);
}
