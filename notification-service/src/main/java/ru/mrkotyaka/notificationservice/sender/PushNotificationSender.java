package ru.mrkotyaka.notificationservice.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushNotificationSender {

    public void send(String pushToken, String text) {
        //todo: can be integrated Firebase FCM
        log.info("Notification successfully sent via PUSH to token {}: {}", pushToken, text);
    }
}
