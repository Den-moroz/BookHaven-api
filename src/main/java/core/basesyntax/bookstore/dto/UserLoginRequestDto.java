package core.basesyntax.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
<<<<<<< HEAD
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotEmpty
        @Length(min = 8, max = 35)
        @Email
        String email,
        @NotEmpty
        @Length(min = 8, max = 35)
=======
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 8, max = 20)
        @Email
        String email,
        @NotEmpty
        @Size(min = 8, max = 20)
>>>>>>> add-docker
        String password
) {
}
