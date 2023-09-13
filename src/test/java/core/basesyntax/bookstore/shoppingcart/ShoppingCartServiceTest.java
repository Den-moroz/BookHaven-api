package core.basesyntax.bookstore.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.shoppingcart.ShoppingCartDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.CartItemService;
import core.basesyntax.bookstore.service.UserService;
import core.basesyntax.bookstore.service.impl.ShoppingCartServiceImpl;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        when(shoppingCartRepository.findById(anyLong()))
                .thenReturn(Optional.of(VALID_SHOPPING_CART));
        when(cartItemService.findByShoppingCartId(VALID_SHOPPING_CART.getId()))
                .thenReturn(new HashSet<>());

        Pageable pageable = PageRequest.of(0, 10);
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(pageable);
        assertNotNull(actual);
        assertEquals(VALID_DTO_RESPONSE, actual);

        verify(userService, times(1)).getUser();
        verify(shoppingCartRepository, times(1)).findById(DEFAULT_USER.getId());
        verify(cartItemService, times(1)).findByShoppingCartId(VALID_SHOPPING_CART.getId());
    }

    @Test
    @DisplayName("Verify get shopping cart method when cart is not found")
    void getShoppingCart_cartNotFound_throwEntityNotFoundException() {
        when(userService.getUser()).thenReturn(DEFAULT_USER);
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.getShoppingCart(PageRequest.of(0, 10));
        });

        verify(userService, times(1)).getUser();
        verify(shoppingCartRepository, times(1)).findById(DEFAULT_USER.getId());
        verify(cartItemService, never()).findByShoppingCartId(anyLong());
    }
}
