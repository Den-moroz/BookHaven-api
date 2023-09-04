package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.item.CartItemDto;
import core.basesyntax.bookstore.dto.item.CreateCartItemDto;
import core.basesyntax.bookstore.dto.item.UpdateCartItemDto;
import java.util.Set;

public interface CartItemService {
    CartItemDto save(CreateCartItemDto cartItemDto);

    Set<CartItemDto> findByShoppingCartId(Long id);

    CartItemDto update(UpdateCartItemDto updateDto, Long id);

    void delete(Long id);
}
