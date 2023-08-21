package core.basesyntax.bookstore.repository.book;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.repository.SpecificationProvider;
import core.basesyntax.bookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can't find correct specifications provider for key "
                                + key));
    }
}
