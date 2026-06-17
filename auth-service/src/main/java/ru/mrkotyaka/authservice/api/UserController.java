package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.authservice.domain.UserProcessor;
import ru.mrkotyaka.authservice.domain.db.UserMapper;
import ru.mrkotyaka.commonlibs.dto.auth.UserRsDto;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProcessor userProcessor;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserRsDto> getAllUsers(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all users");
        adminAccessValidate(authUserRole);
        return userProcessor.getAllUsers();
    }

    @GetMapping("/whoami")
    public UserRsDto getMe(@RequestHeader("X-User-Id") UUID authUserId) {
        log.info("Retrieving users `{}` info", authUserId);
        return userMapper.toUserDto(userProcessor.getUserInfo(authUserId));
    }

    @GetMapping("/{id}")
    public UserRsDto getUserInfo(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.info("Retrieving users info by userId `{}`", id);

        var userInfo = userProcessor.getUserInfo(id);

        if (!userInfo.getId().equals(authUserId) && authUserRole.equals("CUSTOMER")) {
            log.warn("User `{}` tried to get info about userId `{}`",
                    authUserId, userInfo.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this info");
        }
        return userMapper.toUserDto(userInfo);
    }

    private static void adminAccessValidate(String authUserRole) {
        String methodName = MethodHandles.lookup()
                .lookupClass()
                .getEnclosingMethod()
                .getName();

        if (!authUserRole.equals("ADMIN")) {
            log.warn("Access to {} is allowed only to Admins", methodName);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
