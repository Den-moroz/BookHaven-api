package core.basesyntax.bookstore.repository.order;

import core.basesyntax.bookstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
