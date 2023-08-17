package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
