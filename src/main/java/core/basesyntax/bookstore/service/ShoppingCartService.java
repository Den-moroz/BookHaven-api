package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.cart.ShoppingCartDto;
import core.basesyntax.bookstore.dto.item.CartItemDto;
import core.basesyntax.bookstore.dto.item.CreateCartItemDto;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    CartItemDto save(CreateCartItemDto requestDto);

    ShoppingCartDto getShoppingCart(Pageable pageable);
}
