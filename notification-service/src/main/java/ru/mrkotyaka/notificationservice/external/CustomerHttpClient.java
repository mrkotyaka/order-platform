package ru.mrkotyaka.notificationservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mrkotyaka.commonlibs.kafka.notification.CustomerNotificationDto;

@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface CustomerHttpClient {

    @GetMapping("/api/customers/internal/{id}")
    CustomerNotificationDto getById(@PathVariable Long id);
}
