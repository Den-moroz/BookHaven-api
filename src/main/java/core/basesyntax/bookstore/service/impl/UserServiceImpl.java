package core.basesyntax.bookstore.service.impl;

import core.basesyntax.bookstore.dto.UserRegistrationRequestDto;
import core.basesyntax.bookstore.dto.UserRegistrationResponseDto;
import core.basesyntax.bookstore.exception.RegistrationException;
import core.basesyntax.bookstore.mapper.UserMapper;
import core.basesyntax.bookstore.model.Role;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.role.RoleRepository;
import core.basesyntax.bookstore.repository.user.UserRepository;
import core.basesyntax.bookstore.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String ADMIN_EMAIL = "admin";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
<<<<<<< HEAD
        if (userRepository.findByEmailFetchRoles(request.getEmail()).isPresent()) {
=======
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
>>>>>>> add-docker
            throw new RegistrationException("Unable to complete registration");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setShippingAddress(request.getShippingAddress());
        Set<Role> roles = new HashSet<>();
        if (request.getEmail().contains(ADMIN_EMAIL)) {
            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow(
                    () -> new RegistrationException("Can't find such a role "
                            + Role.RoleName.ROLE_ADMIN));
            roles.add(adminRole);
        }
        Role defaultRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(
                () -> new RegistrationException("Can't find such a role "
                        + Role.RoleName.ROLE_USER));
        roles.add(defaultRole);
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}
