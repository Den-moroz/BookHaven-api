package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.dto.cartitem.CreateCartItemDto;
import core.basesyntax.bookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    CartItemDto save(CreateCartItemDto requestDto);

    ShoppingCartDto getShoppingCart();
}
