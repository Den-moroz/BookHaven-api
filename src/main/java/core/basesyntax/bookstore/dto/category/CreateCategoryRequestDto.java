package core.basesyntax.bookstore.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateCategoryRequestDto {
    @NotNull
    @Length(min = 1, max = 25)
    private String name;
    private String description;
}
