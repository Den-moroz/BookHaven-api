package core.basesyntax.bookstore.cartitem;

import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.CartItem;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.cartitem.CartItemRepository;
import java.math.BigDecimal;
import java.sql.Connection;
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
public class CartItemRepositoryTest {
    private static final Book DEFAULT_BOOK = new Book();
    private static final User DEFAULT_USER = new User();
    private static final ShoppingCart VALID_SHOPPING_CART = new ShoppingCart();
    private static final CartItem VALID_CART_ITEM = new CartItem();

    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    void setUp() {
        DEFAULT_BOOK.setId(1L);
        DEFAULT_BOOK.setTitle("Title 1");
        DEFAULT_BOOK.setAuthor("Author 1");
        DEFAULT_BOOK.setIsbn("978-0307743657");
        DEFAULT_BOOK.setPrice(BigDecimal.valueOf(100));

        DEFAULT_USER.setId(1L);
        DEFAULT_USER.setEmail("email@i.ua");
        DEFAULT_USER.setPassword("password");
        DEFAULT_USER.setFirstName("Denis");
        DEFAULT_USER.setLastName("Unknown");

        VALID_CART_ITEM.setId(1L);
        VALID_CART_ITEM.setBook(DEFAULT_BOOK);
        VALID_CART_ITEM.setQuantity(100);
        VALID_CART_ITEM.setShoppingCart(VALID_SHOPPING_CART);

        VALID_SHOPPING_CART.setId(1L);
        VALID_SHOPPING_CART.setUser(DEFAULT_USER);
        VALID_SHOPPING_CART.setCartItems(List.of(VALID_CART_ITEM));
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
    @DisplayName("Find cart items by shopping cart id")
    @Sql(scripts = "classpath:database/cartitem/setup-cart-item-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findCartItemsByShoppingCartId_validShoppingCartId_returnOneItem() {
        Set<CartItem> actual = cartItemRepository.findCartItemsByShoppingCartId(1L);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(Set.of(VALID_CART_ITEM), actual);
    }
}
