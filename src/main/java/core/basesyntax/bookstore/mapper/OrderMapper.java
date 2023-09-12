package core.basesyntax.bookstore.mapper;

import core.basesyntax.bookstore.config.MapperConfig;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import core.basesyntax.bookstore.model.Order;
import core.basesyntax.bookstore.model.OrderItem;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    OrderDto toDto(Order order);

    @AfterMapping
    default void setUserId(@MappingTarget OrderDto orderDto, Order order) {
        orderDto.setUserId(order.getUser().getId());
    }

    @AfterMapping
    default void setOrderItemDto(@MappingTarget OrderDto orderDto, Order order) {
        List<OrderItemDto> orderItemsDto = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setId(orderItem.getId());
            orderItemDto.setQuantity(orderItem.getQuantity());
            orderItemDto.setBookId(orderItem.getBook().getId());
            orderItemsDto.add(orderItemDto);
        }
        orderDto.setOrderItems(orderItemsDto);
    }
}
