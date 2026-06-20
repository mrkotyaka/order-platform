package ru.mrkotyaka.authservice.domain.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mrkotyaka.commonlibs.enums.auth.UserRoles;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Integration test")
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("auth_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    private UserEntity buildUser(String login) {
        return UserEntity.builder()
                .login(login)
                .password("encoded_password")
                .name("Test User")
                .address("Mira, 1")
                .email("test@test.ru")
                .phone("1234567890")
                .role(UserRoles.CUSTOMER)
                .notificationPreference(NotificationPreference.EMAIL)
                .build();
    }

    @Test
    void existsByLogin_shouldReturnTrue_whenUserExists() {
        // arrange
        userRepository.save(buildUser("kilianrow"));

        // act
        var exists = userRepository.existsByLogin("kilianrow");

        // then
        assertTrue(exists);
    }

    @Test
    void existsByLogin_shouldReturnFalse_whenUserNotExists() {
        // when
        var exists = userRepository.existsByLogin("unknown");

        // then
        assertFalse(exists);
    }

    @Test
    void findByLogin_shouldReturnUser_whenExists() {
        // given
        userRepository.save(buildUser("kilianrow"));

        // when
        var found = userRepository.findByLogin("kilianrow");

        // then
        assertTrue(found.isPresent());
        assertEquals("kilianrow", found.get().getLogin());
        assertEquals(UserRoles.CUSTOMER, found.get().getRole());
    }

    @Test
    void findByLogin_shouldReturnEmpty_whenNotExists() {
        // when
        var found = userRepository.findByLogin("unknown");

        // then
        assertTrue(found.isEmpty());
    }

    @Test
    void save_shouldPersistAllFields() {
        // given
        var user = buildUser("kilianrow");

        // when
        var saved = userRepository.save(user);

        // then
        assertNotNull(saved.getId());
        assertEquals("kilianrow", saved.getLogin());
        assertEquals("encoded_password", saved.getPassword());
        assertEquals(UserRoles.CUSTOMER, saved.getRole());
        assertEquals(NotificationPreference.EMAIL, saved.getNotificationPreference());
    }
}
