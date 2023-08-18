package core.basesyntax.bookstore.repository;

import core.basesyntax.bookstore.dto.BookSearchParametersDto;
import core.basesyntax.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<Book> build(BookSearchParametersDto searchParametersDto);
}
