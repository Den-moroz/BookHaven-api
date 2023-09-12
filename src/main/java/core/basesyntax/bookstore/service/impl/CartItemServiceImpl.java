package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.dto.cartitem.CreateCartItemDto;
import core.basesyntax.bookstore.dto.cartitem.UpdateCartItemDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.CartItemMapper;
import core.basesyntax.bookstore.model.CartItem;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.repository.book.BookRepository;
import core.basesyntax.bookstore.repository.cartitem.CartItemRepository;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.CartItemService;
import core.basesyntax.bookstore.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final UserService userService;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    @Transactional
    public CartItemDto save(CreateCartItemDto cartItemDto) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(bookRepository.getById(cartItemDto.getBookId()));
        cartItem.setQuantity(cartItemDto.getQuantity());
        Long id = userService.getAuthenticatedUser().getId();
        setShoppingCartAndCartItems(id, cartItem);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public Set<CartItemDto> findByShoppingCartId(Long id) {
        return cartItemRepository.findCartItemsByShoppingCartId(id).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CartItemDto update(UpdateCartItemDto updateDto, Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a cart item with id " + id)
        );
        cartItem.setQuantity(updateDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long id) {
        cartItemRepository.deleteById(id);
    }

    private void setShoppingCartAndCartItems(Long id, CartItem cartItem) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find shopping cart by id: " + id));
        cartItem.setShoppingCart(shoppingCart);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        if (shoppingCart.getCartItems().isEmpty()) {
            shoppingCart.setCartItems(cartItems);
        } else {
            shoppingCart.getCartItems().add(cartItem);
        }
    }
}
