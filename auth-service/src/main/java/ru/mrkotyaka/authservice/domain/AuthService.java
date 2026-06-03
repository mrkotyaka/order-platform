package ru.mrkotyaka.authservice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.authservice.domain.db.UserCredentials;
import ru.mrkotyaka.authservice.domain.db.UserRepository;
import ru.mrkotyaka.commonlibs.http.auth.AuthRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(AuthRequest request){
        UserCredentials user = UserCredentials.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles("ROLE_USER")
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(AuthRequest request){
        UserCredentials user = userRepository.findByUsername(request.username())
                .orElseThrow(()-> new RuntimeException("Invalid username  or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

}
