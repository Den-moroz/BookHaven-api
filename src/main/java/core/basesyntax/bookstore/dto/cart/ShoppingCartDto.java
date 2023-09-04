package core.basesyntax.bookstore.dto.cart;

import core.basesyntax.bookstore.dto.item.CartItemDto;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
