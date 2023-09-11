package core.basesyntax.bookstore.shoppingcart;

import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.shoppingcart.ShoppingCartDto;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.CartItemService;
import core.basesyntax.bookstore.service.UserService;
import core.basesyntax.bookstore.service.impl.ShoppingCartServiceImpl;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    private static final User DEFAULT_USER = new User();
    private static final ShoppingCart VALID_SHOPPING_CART = new ShoppingCart();
    private static final ShoppingCartDto VALID_DTO_RESPONSE = new ShoppingCartDto();
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private UserService userService;
    @Mock
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        DEFAULT_USER.setId(1L);
        DEFAULT_USER.setEmail("email@i.ua");
        DEFAULT_USER.setPassword("password");
        DEFAULT_USER.setFirstName("Denis");
        DEFAULT_USER.setLastName("Unknown");

        VALID_SHOPPING_CART.setId(1L);
        VALID_SHOPPING_CART.setUser(DEFAULT_USER);

        VALID_DTO_RESPONSE.setId(VALID_SHOPPING_CART.getId());
        VALID_DTO_RESPONSE.setUserId(VALID_SHOPPING_CART.getUser().getId());
        VALID_DTO_RESPONSE.setCartItems(new HashSet<>());
    }

    @Test
    @DisplayName("Verify get shopping cart method")
    void getShoppingCart_includingPagination_returnShoppingCart() {
        when(userService.getUser()).thenReturn(DEFAULT_USER);
        when(shoppingCartRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(VALID_SHOPPING_CART));
        when(cartItemService.findByShoppingCartId(VALID_SHOPPING_CART.getId()))
                .thenReturn(new HashSet<>());

        Pageable pageable = PageRequest.of(0, 10);
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(pageable);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALID_DTO_RESPONSE, actual);
    }
}
