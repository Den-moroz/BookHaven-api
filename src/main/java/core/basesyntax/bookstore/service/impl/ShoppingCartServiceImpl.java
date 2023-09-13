package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.dto.cartitem.CreateCartItemDto;
import core.basesyntax.bookstore.dto.shoppingcart.ShoppingCartDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.model.ShoppingCart;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.shoppingcart.ShoppingCartRepository;
import core.basesyntax.bookstore.service.CartItemService;
import core.basesyntax.bookstore.service.ShoppingCartService;
import core.basesyntax.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemService cartItemService;
    private final UserService userService;

    @Override
    public CartItemDto save(CreateCartItemDto cartDto) {
        return cartItemService.save(cartDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(Pageable pageable) {
        User authenticatedUser = userService.getUser();
        ShoppingCart shoppingCart = shoppingCartRepository
                .findById(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a shopping cart with id"
                        + authenticatedUser.getId()));
        Long id = shoppingCart.getId();
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(id);
        shoppingCartDto.setUserId(authenticatedUser.getId());
        shoppingCartDto.setCartItems(cartItemService.findByShoppingCartId(id));
        return shoppingCartDto;
    }
}
