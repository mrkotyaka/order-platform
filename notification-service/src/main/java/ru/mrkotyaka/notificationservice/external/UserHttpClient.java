package ru.mrkotyaka.notificationservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mrkotyaka.commonlibs.kafka.notification.UserNotificationDto;

import java.util.UUID;

@FeignClient(
        name = "auth-service",
        url = "${auth-service.url}")
public interface UserHttpClient {

    @GetMapping("/api/users/external/{id}")
    UserNotificationDto getById(@PathVariable UUID id);
}
