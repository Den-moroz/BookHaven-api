package core.basesyntax.bookstore.user;

import core.basesyntax.bookstore.model.Role;
import core.basesyntax.bookstore.model.User;
import core.basesyntax.bookstore.repository.user.UserRepository;
import java.sql.Connection;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private static final String VALID_EMAIL = "admin@i.ua";
    private static final User VALID_USER = new User();
    private static final Role VALID_ROLE = new Role();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        VALID_ROLE.setId(1L);
        VALID_ROLE.setName(Role.RoleName.ROLE_ADMIN);

        VALID_USER.setId(1L);
        VALID_USER.setEmail("admin@i.ua");
        VALID_USER.setPassword("password");
        VALID_USER.setFirstName("Denis");
        VALID_USER.setLastName("Unknown");
        VALID_USER.setRoles(Set.of(VALID_ROLE));
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-db.sql")
            );
        }
    }

    @Test
    @DisplayName("Find by email fetch roles")
    @Sql(scripts = "classpath:database/user/setup-user-repository.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByEmailFetchRoles_adminUser_returnUser() {
        User actual = userRepository.findByEmailFetchRoles(VALID_EMAIL).get();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALID_USER, actual);
    }
}
