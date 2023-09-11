package core.basesyntax.bookstore.orderitem;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.Order;
import core.basesyntax.bookstore.model.OrderItem;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.orderitem.OrderItemRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest {
    private static final Long VALID_ORDER_ID = 1L;
    private static final Long VALID_ITEM_ID = 1L;
    private static final Book DEFAULT_BOOK = new Book();
    private static final User DEFAULT_USER = new User();
    private static final Order DEFAULT_ORDER = new Order();
    private static final OrderItem VALID_ORDER_ITEM_1 = new OrderItem();
    private static final OrderItem VALID_ORDER_ITEM_2 = new OrderItem();

    @Autowired
    private OrderItemRepository orderItemRepository;

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

        DEFAULT_ORDER.setId(1L);
        DEFAULT_ORDER.setUser(DEFAULT_USER);
        DEFAULT_ORDER.setStatus(Order.Status.PENDING);
        DEFAULT_ORDER.setOrderDate(LocalDateTime.of(2023, 1, 20, 20, 20, 20));
        DEFAULT_ORDER.setTotal(new BigDecimal("200.00"));
        DEFAULT_ORDER.setShippingAddress("134, Kyiv");

        VALID_ORDER_ITEM_1.setId(1L);
        VALID_ORDER_ITEM_1.setBook(DEFAULT_BOOK);
        VALID_ORDER_ITEM_1.setOrder(DEFAULT_ORDER);
        VALID_ORDER_ITEM_1.setPrice(new BigDecimal("100.00"));
        VALID_ORDER_ITEM_1.setQuantity(1);

        VALID_ORDER_ITEM_2.setId(2L);
        VALID_ORDER_ITEM_2.setBook(DEFAULT_BOOK);
        VALID_ORDER_ITEM_2.setOrder(DEFAULT_ORDER);
        VALID_ORDER_ITEM_2.setPrice(new BigDecimal("100.00"));
        VALID_ORDER_ITEM_2.setQuantity(3);

        DEFAULT_ORDER.setOrderItems(Set.of(VALID_ORDER_ITEM_1, VALID_ORDER_ITEM_2));
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-db.sql")
            );
        }
    }

    @Test
    @DisplayName("Find all order item by order id")
    @Sql(scripts = "classpath:database/orderitem/setup-order-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByOrderId_validOrderId_returnOneItem() {
        List<OrderItem> actual = orderItemRepository.findAllByOrderId(VALID_ORDER_ID);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals(List.of(VALID_ORDER_ITEM_1.getOrder(),
                        VALID_ORDER_ITEM_2.getOrder()),
                List.of(actual.get(0).getOrder(), actual.get(1).getOrder()));
    }

    @Test
    @DisplayName("Find all order item by id and order id")
    @Sql(scripts = "classpath:database/orderitem/setup-order-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAllByIdAndOrderId_validIdAndOrderId_returnOrderItem() {
        OrderItem actual = orderItemRepository.findAllByIdAndOrderId(VALID_ITEM_ID, VALID_ORDER_ID);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALID_ORDER_ITEM_1.getOrder(), actual.getOrder());
    }
}
