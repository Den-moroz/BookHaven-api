package core.basesyntax.bookstore.repository.book;

import core.basesyntax.bookstore.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Book getBookById(Long id);

    List<Book> findAllByCategoryId(Long categoryId);
}
