package core.basesyntax.bookstore.config;

import core.basesyntax.bookstore.model.Role;
import core.basesyntax.bookstore.repository.role.RoleRepository;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Set<Role.RoleName> existingRoleNames = roleRepository.findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<Role.RoleName> newRoleNames = Stream.of(Role.RoleName.values())
                .filter(roleName -> !existingRoleNames.contains(roleName))
                .collect(Collectors.toSet());

        for (Role.RoleName roleName : newRoleNames) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
