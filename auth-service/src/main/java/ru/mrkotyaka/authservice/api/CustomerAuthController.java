package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.authservice.domain.CustomerService;
import ru.mrkotyaka.commonlibs.http.auth.AuthRequestDTO;
import ru.mrkotyaka.commonlibs.http.auth.AuthResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequestDTO request) {
        String token = customerService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
