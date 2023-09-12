package core.basesyntax.bookstore.repository.orderitem;

import core.basesyntax.bookstore.model.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findAllByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.id = :itemId AND oi.order.id = :orderId")
    Optional<OrderItem> findAllByIdAndOrderId(Long itemId, Long orderId);
}
