package core.basesyntax.bookstore.mapper;

import core.basesyntax.bookstore.config.MapperConfig;
import core.basesyntax.bookstore.dto.cart.ShoppingCartDto;
import core.basesyntax.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
