package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public MessageRsDto register(@RequestBody UserRqDto request) {
        return userProcessor.register(request);
    }

    @PostMapping("/login")
    public AuthRsDto login(@RequestBody UserRqDto request) {
        String token = userProcessor.login(request);

        log.info("Login was successful");
        log.debug("Token: {}", token);

        return new AuthRsDto(token);
    }
}
