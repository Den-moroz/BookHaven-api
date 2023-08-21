package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.BookDto;
import core.basesyntax.bookstore.dto.BookSearchParametersDto;
import core.basesyntax.bookstore.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto bookRequestDto);

    List<BookDto> findByParams(BookSearchParametersDto params);

    void deleteById(Long id);
}
