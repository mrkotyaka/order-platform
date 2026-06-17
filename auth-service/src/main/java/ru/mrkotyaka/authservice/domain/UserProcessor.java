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
        log.info("Start registration");
        var entity = UserEntity.builder()
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .address(request.address())
                .email(request.email())
                .phone(request.phone())
                .role(request.role())
                .notificationPreference(request.notificationPreference())
                .build();

        var saved = userRepository.save(entity);
        log.info("User `{}` saved successfully", saved.getId());

        if (saved.getId() != null && (saved.getRole() == UserRoles.COURIER || saved.getRole() == UserRoles.ADMIN)) {
            var courier = deliveryHttpClient.createCourier(userMapper.toCourierRqDto(saved));
            log.info("User `{}` saved successfully like courier `{}`", saved.getId(), courier.courierId());
        }

        return new MessageRsDto("User `" + saved.getId() + "` registered successfully");
    }

    public String login(UserRqDto request) {
        var user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new RuntimeException("Invalid name or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

    public List<UserRsDto> getAllUsers() {
        var allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public UserEntity getUserInfo(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
