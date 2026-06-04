package ru.mrkotyaka.authservice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.authservice.domain.db.UserCredentials;
import ru.mrkotyaka.authservice.domain.db.UserCredentialsMapper;
import ru.mrkotyaka.authservice.domain.db.UserRepository;
import ru.mrkotyaka.commonlibs.http.auth.AuthRequest;
import ru.mrkotyaka.commonlibs.http.auth.UserRoles;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.commonlibs.http.user.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserCredentialsMapper userMapper;

    public String register(AuthRequest request) {

        String adminPassword = Objects.requireNonNull(userRepository.findByUsername("kilian").orElse(null)).getPassword();
        UserRoles role = UserRoles.ROLE_USER;

        if (adminPassword != null && passwordEncoder.matches(request.admin_password(), adminPassword)) {
            role = UserRoles.ROLE_ADMIN;
        }

        UserCredentials user = UserCredentials.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phone(request.phone())
                .roles(role)
                .build();

        userRepository.save(user);
        return "User with " + role + " registered successfully";
    }

    public String login(AuthRequest request) {
        UserCredentials user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

    public List<UserResponse> getAllUsers() {
        List<UserResponse> allUsersDTO = new ArrayList<>();
        var allUsers = userRepository.findAll();
        for(var user : allUsers){
            allUsersDTO.add(userMapper.toUserDto(user));
        }
        return allUsersDTO;
    }
}
