package core.basesyntax.bookstore.order;

import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.order.CreateOrderRequestDto;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.order.UpdateOrderStatusDto;
import core.basesyntax.bookstore.mapper.OrderMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Order;
import core.basesyntax.bookstore.model.OrderItem;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.order.OrderRepository;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.UserService;
import core.basesyntax.bookstore.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    private static final CreateOrderRequestDto VALID_REQUEST =
            new CreateOrderRequestDto("123, Kyiv");
    private static final User DEFAULT_USER = new User();
    private static final Book DEFAULT_BOOK = new Book();
    private static final ShoppingCart DEFAULT_SHOPPING_CART = new ShoppingCart();
    private static final Order VALID_ORDER = new Order();
    private static final OrderDto VALID_ORDER_DTO = new OrderDto();
    private static final UpdateOrderStatusDto VALID_UPDATE_REQUEST =
            new UpdateOrderStatusDto(Order.Status.DELIVERED);
    private static final OrderItem VALID_ORDER_ITEM = new OrderItem();
    private static final Long VALID_ORDER_ID = 1L;

    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private UserService userService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeEach
    void setUp() {
        DEFAULT_USER.setId(1L);
        DEFAULT_USER.setEmail("email@i.ua");
        DEFAULT_USER.setPassword("password");
        DEFAULT_USER.setFirstName("Denis");
        DEFAULT_USER.setLastName("Unknown");

        DEFAULT_BOOK.setId(1L);
        DEFAULT_BOOK.setTitle("Title 1");
        DEFAULT_BOOK.setAuthor("Author 1");
        DEFAULT_BOOK.setIsbn("978-0307743657");
        DEFAULT_BOOK.setPrice(new BigDecimal("100.00"));

        DEFAULT_SHOPPING_CART.setId(1L);
        DEFAULT_SHOPPING_CART.setUser(DEFAULT_USER);
        DEFAULT_SHOPPING_CART.setCartItems(new ArrayList<>());

        VALID_ORDER.setId(1L);
        VALID_ORDER.setStatus(Order.Status.PENDING);
        VALID_ORDER.setUser(DEFAULT_USER);
        VALID_ORDER.setTotal(new BigDecimal("100.00"));
        VALID_ORDER.setShippingAddress(VALID_REQUEST.shippingAddress());
        VALID_ORDER.setOrderDate(LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        VALID_ORDER_DTO.setId(1L);
        VALID_ORDER_DTO.setUserId(VALID_ORDER.getUser().getId());
        VALID_ORDER_DTO.setOrderDate(VALID_ORDER.getOrderDate());
        VALID_ORDER_DTO.setStatus(VALID_ORDER.getStatus().toString());
        VALID_ORDER_DTO.setTotal(VALID_ORDER.getTotal());

        VALID_ORDER_ITEM.setId(1L);
        VALID_ORDER_ITEM.setBook(DEFAULT_BOOK);
        VALID_ORDER_ITEM.setOrder(VALID_ORDER);
        VALID_ORDER_ITEM.setPrice(DEFAULT_BOOK.getPrice());
        VALID_ORDER_ITEM.setQuantity(10);

        VALID_ORDER.setOrderItems(Set.of(VALID_ORDER_ITEM));
    }

    @Test
    @DisplayName("Verify save() method")
    void save_validRequest_returnResponse() {
        when(userService.getUser()).thenReturn(DEFAULT_USER);
        when(orderRepository.save(Mockito.any())).thenReturn(VALID_ORDER);
        when(orderMapper.toDto(Mockito.any())).thenReturn(VALID_ORDER_DTO);
        when(shoppingCartRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(DEFAULT_SHOPPING_CART));

        OrderDto actual = orderService.save(VALID_REQUEST);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALID_ORDER_DTO, actual);
    }

    @Test
    @DisplayName("Verify getAll() method")
    void getAll_validOrder_returnOrder() {
        when(orderRepository.findAll()).thenReturn(List.of(VALID_ORDER));
        when(orderMapper.toDto(Mockito.any())).thenReturn(VALID_ORDER_DTO);

        List<OrderDto> actual = orderService.getAll();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(List.of(VALID_ORDER_DTO), actual);
    }

    @Test
    @DisplayName("Verify update() method")
    void update_validRequest_returnUpdatedOrder() {
        when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(VALID_ORDER));
        when(orderRepository.save(Mockito.any())).thenReturn(VALID_ORDER);
        when(orderMapper.toDto(Mockito.any())).thenReturn(VALID_ORDER_DTO);

        OrderDto actual = orderService.update(VALID_UPDATE_REQUEST, VALID_ORDER_ID);
        VALID_ORDER_DTO.setStatus(VALID_UPDATE_REQUEST.status().toString());
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALID_ORDER_DTO, actual);
    }
}
