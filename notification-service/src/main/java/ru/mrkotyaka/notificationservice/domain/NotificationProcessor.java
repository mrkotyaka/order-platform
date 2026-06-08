package ru.mrkotyaka.notificationservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;
import ru.mrkotyaka.notificationservice.external.CustomerHttpClient;
import ru.mrkotyaka.notificationservice.sender.EmailNotificationSender;
import ru.mrkotyaka.notificationservice.sender.PushNotificationSender;
import ru.mrkotyaka.notificationservice.sender.SmsNotificationSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final CustomerHttpClient customerHttpClient;
    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final PushNotificationSender pushSender;

    @KafkaListener(topics = "${notification-topic}", groupId = "notification-service")
    public void handle(NotificationEvent event) {
        log.info("Received notification event: customerId={}, type={}",
                event.customerId(), event.type());

        try {
            var customer = customerHttpClient.getById(event.customerId());

            if (customer == null) {
                log.error("Customer not found for id: {}", event.customerId());
                return;
            }

            switch (customer.notificationPreference()) {
                case EMAIL -> emailSender.send(customer.email(), event.payload());
                case SMS -> smsSender.send(customer.phone(), event.payload());
                case PUSH -> pushSender.send(customer.pushToken(), event.payload());
            }
        } catch (Exception e) {
            log.error("Failed to send notification: customerId={}, error={}",
                    event.customerId(), e.getMessage());
        }
    }
}
