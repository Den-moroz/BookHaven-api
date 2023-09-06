package core.basesyntax.bookstore.security;

import core.basesyntax.bookstore.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
<<<<<<< HEAD
        return userRepository.findByEmailFetchRoles(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + username));
    }
=======
        return userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + username));
    }

>>>>>>> add-docker
}
