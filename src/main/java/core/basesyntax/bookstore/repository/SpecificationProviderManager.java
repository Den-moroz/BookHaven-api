package core.basesyntax.bookstore.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProviderManager<T> {
    SpecificationProvider<T> getSpecificationProvider(String key);
}
