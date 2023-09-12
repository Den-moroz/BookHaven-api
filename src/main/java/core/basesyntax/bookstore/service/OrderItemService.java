package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import java.util.List;

public interface OrderItemService {
    List<OrderItemDto> getAllByOrderId(Long orderId);

    OrderItemDto getByItemIdAndOrderId(Long itemId, Long orderId);
}
