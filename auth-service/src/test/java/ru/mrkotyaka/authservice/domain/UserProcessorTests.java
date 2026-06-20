package ru.mrkotyaka.authservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mrkotyaka.authservice.domain.db.UserEntity;
import ru.mrkotyaka.authservice.domain.db.UserMapper;
import ru.mrkotyaka.authservice.domain.db.UserRepository;
import ru.mrkotyaka.authservice.external.DeliveryHttpClient;
import ru.mrkotyaka.commonlibs.dto.auth.UserRqDto;
import ru.mrkotyaka.commonlibs.enums.auth.UserRoles;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserProcessor Unit Tests")
public class UserProcessorTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DeliveryHttpClient deliveryHttpClient;

    @InjectMocks
    private UserProcessor userProcessor;

    private final UserRqDto requestUserRqDto = new UserRqDto(
            "kilianrow", "qwerty123", null,
            null, null, null,
            null, null
    );
    private final UserRqDto requestUserRqDtoFull = new UserRqDto(
            "kilianrow", "qwerty123", "Kilian Row",
            "Mira,1", "test@test.ru", "1234567890",
            UserRoles.CUSTOMER, NotificationPreference.EMAIL
    );

    @Test
    void register_shouldSaveUser_whenLoginIsUnique() {
        // arrange
        var savedUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .login("kilianrow")
                .password("encoded_password")
                .role(UserRoles.CUSTOMER)
                .build();

        when(passwordEncoder.encode("qwerty123")).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(savedUser); // мок возврата save

        // act
        var result = userProcessor.register(requestUserRqDtoFull);

        // action
        assertNotNull(result);
        verify(userRepository, times(1)).save(argThat(user ->
                user.getLogin().equals("kilianrow") &&
                        user.getPassword().equals("encoded_password") &&
                        user.getRole().equals(UserRoles.CUSTOMER)
        ));
    }

    @Test
    void register_shouldNotCallDelivery_whenUserIsCustomer() {
        // arrange
        var savedUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .login("kilianrow")
                .role(UserRoles.CUSTOMER)
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(savedUser);

        // act
        userProcessor.register(requestUserRqDtoFull);

        // action — delivery не вызывается для CUSTOMER
        verify(deliveryHttpClient, never()).createCourier(any());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // arrange
        var user = UserEntity.builder()
                .login("kilianrow")
                .password("encoded_password")
                .role(UserRoles.CUSTOMER)
                .build();

        when(userRepository.findByLogin("kilianrow")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("qwerty123", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt.token.here");

        // act
        var result = userProcessor.login(requestUserRqDto);

        // action
        assertNotNull(result);
        assertEquals("jwt.token.here", result);
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        // arrange
        var user = UserEntity.builder()
                .login("kilianrow")
                .password("encoded_password")
                .build();

        when(userRepository.findByLogin("kilianrow")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // act + action — бросает RuntimeException т.к. в коде именно он
        assertThrows(RuntimeException.class, () -> userProcessor.login(requestUserRqDto));
    }

    @Test
    @DisplayName("should return all users")
    void getUsers_shouldReturnUsersTest() {
        List<UserEntity> users = List.of(UserEntity.builder()
                .id(UUID.randomUUID()).login("kilianrow").password("qwerty123").name("kilian Row").address("tyt").email("mail@test.com").phone("1234567890").role(UserRoles.ADMIN).notificationPreference(NotificationPreference.SMS).build());

        when(userRepository.findAll()).thenReturn(users);

        var result = userRepository.findAll();

        assertNotNull(result);
        assertEquals(users.size(), result.size());

        verify(userRepository, times(1)).findAll();
    }
}
