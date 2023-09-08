package core.basesyntax.bookstore.repository.book;

import core.basesyntax.bookstore.model.Book;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b INNER JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findAllByCategoriesIdWithCategories(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT b FROM Book b INNER JOIN FETCH b.categories")
    List<Book> findAllWithCategories(Pageable pageable);
}
