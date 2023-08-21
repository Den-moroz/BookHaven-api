package core.basesyntax.bookstore.repository.book;

import core.basesyntax.bookstore.dto.BookSearchParametersDto;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.SpecificationBuilder;
import core.basesyntax.bookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements
        SpecificationBuilder<Book, BookSearchParametersDto> {
    private static final String KEY_FOR_TITLE = "title";
    private static final String KEY_FOR_AUTHOR = "author";
    private static final String KEY_FOR_PRICE_FROM = "fromPrice";
    private static final String KEY_FOR_PRICE_TO = "toPrice";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (searchParametersDto.title() != null && searchParametersDto.title().length > 0) {
            specification = specification.and(createSpecification(KEY_FOR_TITLE,
                    searchParametersDto.title()));
        }
        if (searchParametersDto.author() != null && searchParametersDto.author().length > 0) {
            specification = specification.and(createSpecification(KEY_FOR_AUTHOR,
                    searchParametersDto.author()));
        }
        if (searchParametersDto.fromPrice() != null) {
            specification = specification.and(createSpecification(KEY_FOR_PRICE_FROM,
                    searchParametersDto.fromPrice()));
        }
        if (searchParametersDto.toPrice() != null) {
            specification = specification.and(createSpecification(KEY_FOR_PRICE_TO,
                    searchParametersDto.toPrice()));
        }

        return specification;
    }

    private Specification<Book> createSpecification(String key, Object... value) {
        return bookSpecificationProviderManager
                    .getSpecificationProvider(key)
                    .getSpecification(value);
    }
}
