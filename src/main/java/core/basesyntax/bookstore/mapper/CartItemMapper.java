package core.basesyntax.bookstore.mapper;

import core.basesyntax.bookstore.config.MapperConfig;
import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);

    @AfterMapping
    default void setBookTitle(@MappingTarget CartItemDto cartDto, CartItem cartItem) {
        cartDto.setBookTitle(cartItem.getBook().getTitle());
    }

    @AfterMapping
    default void setBookId(@MappingTarget CartItemDto cartDto, CartItem cartItem) {
        cartDto.setBookId(cartItem.getBook().getId());
    }
}
