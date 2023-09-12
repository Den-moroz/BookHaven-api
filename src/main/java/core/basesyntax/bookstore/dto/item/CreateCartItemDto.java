package core.basesyntax.bookstore.dto.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemDto {
    @NotNull
    private Long bookId;
    @Min(1)
    private Integer quantity;
}
