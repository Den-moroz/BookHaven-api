package core.basesyntax.bookstore.repository.book.spec;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ToPriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "toPrice";
    private static final String NAME_OF_COLUMN = "price";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(Object... params) {
        Integer toPrice = (Integer) params[0];
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .lessThanOrEqualTo(root.get(NAME_OF_COLUMN), toPrice);
    }
}
