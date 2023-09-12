package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.order.CreateOrderRequestDto;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.order.UpdateOrderStatusDto;
import java.util.List;

public interface OrderService {
    OrderDto save(CreateOrderRequestDto requestDto);

    List<OrderDto> getAll();

    OrderDto update(UpdateOrderStatusDto updateOrderStatusDto, Long userId);
}
