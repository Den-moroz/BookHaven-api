package core.basesyntax.bookstore.orderitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.OrderItemMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Order;
import core.basesyntax.bookstore.model.OrderItem;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.orderitem.OrderItemRepository;
import core.basesyntax.bookstore.service.impl.OrderItemServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {
    private static final Long VALID_ORDER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Book DEFAULT_BOOK = new Book();
    private static final User DEFAULT_USER = new User();
    private static final Order VALID_ORDER = new Order();
    private static final OrderItem VALID_ORDER_ITEM = new OrderItem();
    private static final OrderItemDto VALID_ORDER_ITEM_DTO = new OrderItemDto();

    @InjectMocks
    private OrderItemServiceImpl orderItemService;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setUp() {
        DEFAULT_BOOK.setId(1L);
        DEFAULT_BOOK.setTitle("Title 1");
        DEFAULT_BOOK.setAuthor("Author 1");
        DEFAULT_BOOK.setIsbn("978-0307743657");
        DEFAULT_BOOK.setPrice(new BigDecimal("100.00"));

        DEFAULT_USER.setId(1L);
        DEFAULT_USER.setEmail("email@i.ua");
        DEFAULT_USER.setPassword("password");
        DEFAULT_USER.setFirstName("Denis");
        DEFAULT_USER.setLastName("Unknown");

        VALID_ORDER.setId(1L);
        VALID_ORDER.setUser(DEFAULT_USER);
        VALID_ORDER.setStatus(Order.Status.PENDING);
        VALID_ORDER.setTotal(new BigDecimal("100.00"));
        VALID_ORDER.setOrderDate(LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        VALID_ORDER_ITEM.setId(1L);
        VALID_ORDER_ITEM.setBook(DEFAULT_BOOK);
        VALID_ORDER_ITEM.setPrice(new BigDecimal("100.00"));
        VALID_ORDER_ITEM.setQuantity(100);
        VALID_ORDER_ITEM.setOrder(VALID_ORDER);

        VALID_ORDER.setOrderItems(Set.of(VALID_ORDER_ITEM));

        VALID_ORDER_ITEM_DTO.setId(1L);
        VALID_ORDER_ITEM_DTO.setBookId(VALID_ORDER_ITEM.getBook().getId());
        VALID_ORDER_ITEM_DTO.setQuantity(VALID_ORDER_ITEM.getQuantity());

    }

    @Test
    @DisplayName("Verify getAllByOrderId() method")
    void getAllByOrderId_validOrderId_returnOneDto() {
        when(orderItemRepository.findAllByOrderId(anyLong()))
                .thenReturn(List.of(VALID_ORDER_ITEM));
        when(orderItemMapper.toDto(any())).thenReturn(VALID_ORDER_ITEM_DTO);

        List<OrderItemDto> actual = orderItemService.getAllByOrderId(VALID_ORDER_ID);
        assertNotNull(actual);
        assertEquals(List.of(VALID_ORDER_ITEM_DTO), actual);
    }

    @Test
    @DisplayName("Verify getAllByOrderId() with no order items")
    void getAllByOrderId_noItems_returnEmptyList() {
        when(orderItemRepository.findAllByOrderId(anyLong()))
                .thenReturn(Collections.emptyList());

        List<OrderItemDto> actual = orderItemService.getAllByOrderId(VALID_ORDER_ID);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("Verify getByItemIdAndOrderId() method")
    void getByItemIdAndOrderId_validItemIdAndOrderId_returnOneItem() {
        when(orderItemRepository.findAllByIdAndOrderId(anyLong(), anyLong()))
                .thenReturn(Optional.of(VALID_ORDER_ITEM));
        when(orderItemMapper.toDto(any())).thenReturn(VALID_ORDER_ITEM_DTO);

        OrderItemDto actual = orderItemService.getByItemIdAndOrderId(VALID_ITEM_ID, VALID_ORDER_ID);
        assertNotNull(actual);
        assertEquals(VALID_ORDER_ITEM_DTO, actual);
    }

    @Test
    @DisplayName("Verify getByItemIdAndOrderId() with non-existent item")
    void getByItemIdAndOrderId_nonExistentItem_throwEntityNotFoundException() {
        when(orderItemRepository.findAllByIdAndOrderId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderItemService.getByItemIdAndOrderId(VALID_ITEM_ID, VALID_ORDER_ID));
    }
}
