package core.basesyntax.bookstore.service;

import core.basesyntax.bookstore.dto.UserRegistrationRequestDto;
import core.basesyntax.bookstore.dto.UserResponseDto;
import core.basesyntax.bookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
