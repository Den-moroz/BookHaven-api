package core.basesyntax.bookstore.controller;

import core.basesyntax.bookstore.dto.order.CreateOrderRequestDto;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.order.UpdateOrderStatusDto;
import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import core.basesyntax.bookstore.service.OrderItemService;
import core.basesyntax.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing order")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @PostMapping
    @Operation(summary = "Make an new order")
    public OrderDto placeAnOrder(@RequestBody CreateOrderRequestDto orderRequestDto) {
        return orderService.save(orderRequestDto);
    }

    @GetMapping
    @Operation(summary = "Return a list of user order's history",
            description = "Pagination and sorting are also included")
    public List<OrderDto> getAll() {
        return orderService.getAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update order status")
    public OrderDto update(@RequestBody @Valid UpdateOrderStatusDto updateOrderStatusDto,
                               @PathVariable Long id) {
        return orderService.update(updateOrderStatusDto, id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Return a list of order items by order id")
    public List<OrderItemDto> getAllByOrderId(@PathVariable Long orderId) {
        return orderItemService.getAllByOrderId(orderId);
    }

    @GetMapping("{orderId}/items/{itemId}")
    @Operation(summary = "Return an order item",
            description = "Retrieve a specific order item within an order")
    public OrderItemDto getByIdAndOrderId(@PathVariable Long itemId, @PathVariable Long orderId) {
        return orderItemService.getByItemIdAndOrderId(itemId, orderId);
    }
}
