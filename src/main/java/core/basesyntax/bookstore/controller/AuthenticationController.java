package core.basesyntax.bookstore.controller;

import core.basesyntax.bookstore.dto.user.UserLoginRequestDto;
import core.basesyntax.bookstore.dto.user.UserLoginResponseDto;
import core.basesyntax.bookstore.dto.user.UserRegistrationRequestDto;
import core.basesyntax.bookstore.dto.user.UserRegistrationResponseDto;
import core.basesyntax.bookstore.exception.RegistrationException;
import core.basesyntax.bookstore.security.AuthenticationService;
import core.basesyntax.bookstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/register")
    @ResponseBody
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
