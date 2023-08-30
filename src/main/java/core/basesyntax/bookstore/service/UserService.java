package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.UserRegistrationRequestDto;
import core.basesyntax.bookstore.dto.UserRegistrationResponseDto;
import core.basesyntax.bookstore.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException;
}
