package com.acme.notifications.demo.config;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import com.acme.notifications.hub.dispatcher.infrastructure.client.email.MailgunEmailClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.email.SendGridEmailClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.push.FcmPushClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.slack.SlackApiClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.sms.TwilioSmsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Configuration
public class NotificationHubConfig {

    @Bean
    public NotificationConfig notificationConfig() {
        return new NotificationConfigBuilder()
                .defaultFromEmail("noreply@acme.com")
                .defaultFromSms("+12345678900")
                // Proveedores simulados
//                .registerEmail("sendgrid", new SendGridEmailClient("SG.xxxxxx"))
                .registerEmail("mailgun", new MailgunEmailClient("key-xxxx", "mg.acme.com"))
                .registerSms("twilio", new TwilioSmsClient("ACxxxx", "authxxxx"))
                .registerPush("fcm", new FcmPushClient("/path/service-account.json"))
                .registerSlack("slack", new SlackApiClient("xoxb-***"))
                // Prioridades y fallback por canal
                .selection(new PrimaryFallbackProviderSelection(Map.of(
                        ChannelType.EMAIL, List.of("sendgrid", "mailgun"),
                        ChannelType.SMS,   List.of("twilio"),
                        ChannelType.PUSH,  List.of("fcm"),
                        ChannelType.SLACK, List.of("slack")
                )))
                // Reintentos + backoff
                .retry(NotificationConfigBuilder.fixedRetry(3, 200))
                // Asíncrono opcional
                .async(NotificationConfigBuilder.async(Executors.newFixedThreadPool(4)))
                .build();
    }

    @Bean
    public NotificationFacade notificationFacade(NotificationConfig config) {
        return new NotificationFacadeImpl(config);
    }
}