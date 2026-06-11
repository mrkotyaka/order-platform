package ru.mrkotyaka.authservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.authservice.domain.db.UserEntity;
import ru.mrkotyaka.authservice.domain.db.UserMapper;
import ru.mrkotyaka.authservice.domain.db.UserRepository;
import ru.mrkotyaka.authservice.external.DeliveryHttpClient;
import ru.mrkotyaka.commonlibs.dto.auth.MessageRsDto;
import ru.mrkotyaka.commonlibs.dto.auth.UserRqDto;
import ru.mrkotyaka.commonlibs.dto.auth.UserRsDto;
import ru.mrkotyaka.commonlibs.enums.auth.UserRoles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProcessor {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final DeliveryHttpClient deliveryHttpClient;

    public MessageRsDto register(UserRqDto request) {
        log.info("=== REGISTER START ===");
        log.info("Request login: {}", request.login());
        log.info("Request name: {}", request.name());

        UserEntity user = UserEntity.builder()
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .address(request.address())
                .email(request.email())
                .phone(request.phone())
                .role(request.role())
                .notificationPreference(request.notificationPreference())
                .build();

        userRepository.save(user);
        log.info("User `{}` saved successfully", user.getId());

        if (user.getId() != null && (user.getRole() == UserRoles.COURIER || user.getRole() == UserRoles.ADMIN)) {
            var courier = deliveryHttpClient.createCourier(userMapper.toCourierRqDto(user));
            log.info("User `{}` saved successfully like courier `{}`", user.getId(), courier.userId());
        }

        return new MessageRsDto("User registered successfully!");
    }

    public String login(UserRqDto request) {
        UserEntity user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new RuntimeException("Invalid name or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

    public List<UserRsDto> getAllUsers() {
        List<UserRsDto> allUsersDto = new ArrayList<>();
        var allUsers = userRepository.findAll();
        for (var user : allUsers) {
            allUsersDto.add(userMapper.toUserDto(user));
        }
        return allUsersDto;
    }

    public UserEntity getUserInfo(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
