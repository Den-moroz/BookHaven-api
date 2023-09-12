package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.order.CreateOrderRequestDto;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.order.UpdateOrderStatusDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.OrderMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.CartItem;
import core.basesyntax.bookstore.model.Order;
import core.basesyntax.bookstore.model.OrderItem;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.order.OrderRepository;
import core.basesyntax.bookstore.repository.orderitem.OrderItemRepository;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.OrderService;
import core.basesyntax.bookstore.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto save(CreateOrderRequestDto requestDto) {
        User user = userService.getUser();
        List<CartItem> cartItems = getCartItems(user);
        BigDecimal totalPrice = calculateTotalPrice(cartItems);
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestDto.shippingAddress());
        order.setTotal(totalPrice);
        order = orderRepository.save(order);
        Set<OrderItem> orderItems = parseToOrderItem(cartItems, order);
        order.setOrderItems(orderItems);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public List<OrderDto> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto update(UpdateOrderStatusDto updateOrderStatusDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find a order with id " + orderId)
        );
        order.setStatus(updateOrderStatusDto.status());
        return orderMapper.toDto(orderRepository.save(order));
    }

    private List<CartItem> getCartItems(User user) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a shopping cart with id"
                        + user.getId()));
        return shoppingCart.getCartItems();
    }

    private Set<OrderItem> parseToOrderItem(List<CartItem> cartItems, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItemRepository.save(orderItem));
        }
        return orderItems;
    }

    private BigDecimal calculateTotalPrice(List<CartItem> orderItems) {
        return orderItems.stream()
                .map(CartItem::getBook)
                .map(Book::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
