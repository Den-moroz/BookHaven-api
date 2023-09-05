package core.basesyntax.bookstore.dto.order;

import core.basesyntax.bookstore.model.Order;

public record UpdateOrderStatusDto(Order.Status status) {
}
