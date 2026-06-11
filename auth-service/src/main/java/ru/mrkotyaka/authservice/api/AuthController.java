package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.authservice.domain.UserProcessor;
import ru.mrkotyaka.commonlibs.dto.auth.AuthRsDto;
import ru.mrkotyaka.commonlibs.dto.auth.MessageRsDto;
import ru.mrkotyaka.commonlibs.dto.auth.UserRqDto;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserProcessor userProcessor;

    @PostMapping("/register")
    public ResponseEntity<MessageRsDto> register(
            @RequestBody UserRqDto request
    ) {
        return ResponseEntity.ok(userProcessor.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthRsDto> login(
            @RequestBody UserRqDto request
    ) {
        String token = userProcessor.login(request);

        log.info("Login was successful. Token: {}", token);

        return ResponseEntity.ok(new AuthRsDto(token));
    }
}
