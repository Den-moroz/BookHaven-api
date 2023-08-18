package core.basesyntax.bookstore.repository.book.spec;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get("title").in(Arrays.stream(params).toArray());
    }
}
