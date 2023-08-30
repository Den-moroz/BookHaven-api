package core.basesyntax.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBookRequestDto {
    @NotNull
    @Length(min = 1, max = 255)
    private String title;
    @NotNull
    @Length(min = 1, max = 255)
    private String author;
    @NotNull
    @ISBN
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    @Length(min = 10, max = 255)
    private String description;
    private String coverImage;
}
