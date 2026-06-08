package ru.mrkotyaka.notificationservice.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsNotificationSender {

    public void send(String phone, String text) {
        // TODO: can be integrated Twilio or SMSC.ru
        log.info("Notification successfully sent via SMS to {}: {}", phone, text);
    }
}
