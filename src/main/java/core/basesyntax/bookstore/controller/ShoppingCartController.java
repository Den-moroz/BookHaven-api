package core.basesyntax.bookstore.controller;

import core.basesyntax.bookstore.dto.cart.ShoppingCartDto;
import core.basesyntax.bookstore.dto.item.CartItemDto;
import core.basesyntax.bookstore.dto.item.CreateCartItemDto;
import core.basesyntax.bookstore.dto.item.UpdateCartItemDto;
import core.basesyntax.bookstore.service.CartItemService;
import core.basesyntax.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @PostMapping
    @Operation(summary = "Create a new shopping cart")
    public CartItemDto addCartItem(@RequestBody @Valid CreateCartItemDto cartItemDto) {
        return shoppingCartService.save(cartItemDto);
    }

    @GetMapping
    @Operation(summary = "Get a shopping cart",
            description = "Pagination and sorting are also included")
    public ShoppingCartDto getShoppingCart(Pageable pageable) {
        return shoppingCartService.getShoppingCart(pageable);
    }

    @PutMapping("/cart-items/{id}")
    @Operation(summary = "Update quantity of cartItem")
    public CartItemDto update(@RequestBody @Valid UpdateCartItemDto updateCartItemDto,
                              @PathVariable Long id) {
        return cartItemService.update(updateCartItemDto, id);
    }

    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "Delete cart item by id")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.delete(cartItemId);
    }
}
