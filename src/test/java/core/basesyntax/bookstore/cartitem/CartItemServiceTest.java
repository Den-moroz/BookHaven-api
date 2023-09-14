package core.basesyntax.bookstore.cartitem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.dto.cartitem.CreateCartItemDto;
import core.basesyntax.bookstore.dto.cartitem.UpdateCartItemDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.CartItemMapper;
import core.basesyntax.bookstore.model.Book;
import core.basesyntax.bookstore.model.CartItem;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.cartitem.CartItemRepository;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.UserService;
import core.basesyntax.bookstore.service.impl.CartItemServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
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
public class CartItemServiceTest {
    private static final Book DEFAULT_BOOK = new Book();
    private static final User DEFAULT_USER = new User();
    private static final ShoppingCart DEFAULT_SHOPPING_CART = new ShoppingCart();
    private static final CreateCartItemDto VALID_REQUEST = new CreateCartItemDto();
    private static final CartItemDto VALID_RESPONSE_DTO = new CartItemDto();
    private static final CartItem VALID_CART_ITEM = new CartItem();
    private static final UpdateCartItemDto VALID_UPDATE_REQUEST = new UpdateCartItemDto();
    private static final Long VALID_SHOPPING_CART_ID = 1L;
    private static final Long INVALID_ID = -1L;

    @InjectMocks
    private CartItemServiceImpl cartItemService;
    @Mock
    private UserService userService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

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

        DEFAULT_SHOPPING_CART.setId(1L);
        DEFAULT_SHOPPING_CART.setUser(DEFAULT_USER);
        DEFAULT_SHOPPING_CART.setCartItems(new ArrayList<>());

        VALID_REQUEST.setBookId(DEFAULT_BOOK.getId());
        VALID_REQUEST.setQuantity(2);

        VALID_CART_ITEM.setId(1L);
        VALID_CART_ITEM.setBook(DEFAULT_BOOK);
        VALID_CART_ITEM.setQuantity(VALID_REQUEST.getQuantity());
        VALID_CART_ITEM.setShoppingCart(DEFAULT_SHOPPING_CART);

        VALID_RESPONSE_DTO.setId(VALID_CART_ITEM.getId());
        VALID_RESPONSE_DTO.setBookId(VALID_CART_ITEM.getId());
        VALID_RESPONSE_DTO.setBookTitle(VALID_CART_ITEM.getBook().getTitle());
        VALID_RESPONSE_DTO.setQuantity(VALID_CART_ITEM.getQuantity());

        VALID_UPDATE_REQUEST.setQuantity(100);
    }

    @Test
    @DisplayName("Verify save() method")
    void save_validRequest_returnDto() {
        when(bookRepository.getById(anyLong())).thenReturn(DEFAULT_BOOK);
        when(userService.getAuthenticatedUser()).thenReturn(DEFAULT_USER);
        when(shoppingCartRepository.findById(anyLong()))
                .thenReturn(Optional.of(DEFAULT_SHOPPING_CART));
        when(cartItemMapper.toDto(any())).thenReturn(VALID_RESPONSE_DTO);
        when(cartItemRepository.save(any())).thenReturn(any());

        CartItemDto actual = cartItemService.save(VALID_REQUEST);
        assertNotNull(actual);
        assertEquals(VALID_RESPONSE_DTO, actual);

        verify(bookRepository, times(1)).getById(anyLong());
        verify(userService, times(1)).getAuthenticatedUser();
        verify(shoppingCartRepository, times(1)).findById(anyLong());
        verify(cartItemMapper, times(1)).toDto(any());
        verify(cartItemRepository, times(1)).save(any());
        verifyNoMoreInteractions(cartItemMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Verify save() method with non-existent shopping cart")
    void save_nonExistentShoppingCart_throwException() {
        when(userService.getAuthenticatedUser()).thenReturn(new User());

        assertThrows(EntityNotFoundException.class, () -> cartItemService.save(VALID_REQUEST));
    }

    @Test
    @DisplayName("Verify findByShoppingCartId() method")
    void findByShoppingCartId_validId_returnOneItem() {
        when(cartItemRepository.findCartItemsByShoppingCartId(anyLong()))
                .thenReturn(Set.of(VALID_CART_ITEM));
        when(cartItemMapper.toDto(any())).thenReturn(VALID_RESPONSE_DTO);

        Set<CartItemDto> actual = cartItemService.findByShoppingCartId(VALID_SHOPPING_CART_ID);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(Set.of(VALID_RESPONSE_DTO), actual);

        verify(cartItemRepository, times(1))
                .findCartItemsByShoppingCartId(anyLong());
        verify(cartItemMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartItemMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Verify update() method")
    void update_validUpdateRequest_returnResponse() {
        when(cartItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(VALID_CART_ITEM));
        when(cartItemRepository.save(any())).thenReturn(VALID_CART_ITEM);
        when(cartItemMapper.toDto(any())).thenReturn(VALID_RESPONSE_DTO);

        CartItemDto actual = cartItemService.update(VALID_UPDATE_REQUEST, 1L);
        VALID_RESPONSE_DTO.setQuantity(VALID_UPDATE_REQUEST.getQuantity());
        assertNotNull(actual);
        assertEquals(VALID_RESPONSE_DTO, actual);

        verify(cartItemRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any());
        verify(cartItemMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartItemMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Verify update() method with non-existent cart item")
    void update_nonExistentCartItem_throwException() {
        assertThrows(EntityNotFoundException.class,
                () -> cartItemService.update(VALID_UPDATE_REQUEST, INVALID_ID));
    }

    @Test
    @DisplayName("Verify delete() method with non-existent cart item")
    void delete_nonExistentCartItem_throwException() {
        assertThrows(EntityNotFoundException.class,
                () -> cartItemService.delete(INVALID_ID));
    }
}
