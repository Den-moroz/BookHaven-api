package core.basesyntax.bookstore.repository.book.spec;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class FromPriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "fromPrice";
    private static final String NAME_OF_COLUMN = "price";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        Integer fromPrice = (Integer) params[0];
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get(NAME_OF_COLUMN), fromPrice);
    }
}
