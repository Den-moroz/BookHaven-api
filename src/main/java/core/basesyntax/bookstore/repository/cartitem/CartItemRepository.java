package core.basesyntax.bookstore.repository.cartitem;

import core.basesyntax.bookstore.model.CartItem;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.shoppingCart.id = :shoppingCartId")
    Set<CartItem> findCartItemsByShoppingCartId(Long shoppingCartId);
}
