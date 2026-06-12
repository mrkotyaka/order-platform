package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.authservice.domain.UserProcessor;
import ru.mrkotyaka.commonlibs.kafka.notification.UserNotificationDto;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users/external")
@RequiredArgsConstructor
public class ExternalController {

    private final UserProcessor userProcessor;

    @GetMapping("/{id}")
    public UserNotificationDto getUserForNotification(
            @PathVariable UUID id
    ) {
        var user = userProcessor.getUserInfo(id);
        return new UserNotificationDto(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                null, // pushToken пока null
                user.getNotificationPreference()
        );
    }
}
