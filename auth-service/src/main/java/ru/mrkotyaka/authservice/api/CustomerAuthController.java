package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.authservice.domain.CustomerService;
import ru.mrkotyaka.commonlibs.http.auth.AuthRqDto;
import ru.mrkotyaka.commonlibs.http.auth.AuthResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRqDto request) {
        return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRqDto request) {
        String token = customerService.login(request);

        log.info("Login was successful. Token: {}", token);

        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}
