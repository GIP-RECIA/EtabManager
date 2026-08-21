package fr.recia.manager.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import fr.recia.notifications.event_rest_client_kafka.HttpNotificationClient;


@Data
@ConfigurationProperties(prefix = "notification-config")
@Configuration
public class NotificationConfiguration {
    String url;
    String service;
    String apiKey;
    String titleFonction;
    String messageFonction;
    String titlePassword;
    String messagePassword;

    @Bean
    HttpNotificationClient notificationClient() {
        return new HttpNotificationClient(url, service, apiKey);
    }
}
