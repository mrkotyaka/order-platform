package ru.mrkotyaka.notificationservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;
import ru.mrkotyaka.notificationservice.external.UserHttpClient;
import ru.mrkotyaka.notificationservice.sender.EmailNotificationSender;
import ru.mrkotyaka.notificationservice.sender.PushNotificationSender;
import ru.mrkotyaka.notificationservice.sender.SmsNotificationSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final UserHttpClient userHttpClient;
    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final PushNotificationSender pushSender;

    @KafkaListener(topics = "${notification-topic}", groupId = "notification-service")
    public void handle(NotificationEvent event) {
        log.info("Received notification event: userId={}, type={}",
                event.userId(), event.type());

        try {
            var user = userHttpClient.getById(event.userId());

            if (user == null) {
                log.error("Recipient with id='{}' not found", event.userId());
                return;
            }

            if (user.email() == null) {
                log.error("Email is not filled for recipient `{}`", event.userId());
                return;
            }

            switch (user.notificationPreference()) {
                case EMAIL -> emailSender.send(user.email(), event.message());
                case SMS -> smsSender.send(user.phone(), event.message());
                case PUSH -> pushSender.send(user.pushToken(), event.message());
            }
        } catch (Exception e) {
            log.error("Failed to send notification: recipient id={}, error={}",
                    event.userId(), e.getMessage());
        }
    }
}
