package core.basesyntax.bookstore.repository.orderitem;

import core.basesyntax.bookstore.model.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi INNER JOIN FETCH oi.order o WHERE o.id = :orderId")
    List<OrderItem> findAllByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi INNER JOIN FETCH oi.order o WHERE oi.id = :itemId "
            + "AND o.id = :orderId")
    OrderItem findAllByIdAndOrderId(Long itemId, Long orderId);
}
