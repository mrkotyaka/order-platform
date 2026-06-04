package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.authservice.domain.AuthService;
import ru.mrkotyaka.commonlibs.http.auth.AuthResponse;
import ru.mrkotyaka.commonlibs.http.user.UserResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping
    public List<UserResponse> getAllUsers(){
        log.info("Retrieving all users from the flow");
        return authService.getAllUsers();
    }
}
