package com.acme.notifications.hub.dispatcher.examples;

import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.*;
import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.infrastructure.client.email.SendGridEmailClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.email.MailgunEmailClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.sms.TwilioSmsClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.push.FcmPushClient;
import com.acme.notifications.hub.dispatcher.infrastructure.client.slack.SlackApiClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public final class NotificationExamples {
    public static void main(String[] args) {
        NotificationConfig cfg = new NotificationConfigBuilder()
                .defaultFromEmail("noreply@acme.com")
                .defaultFromSms("+12345678900")
                .registerEmail("sendgrid", new SendGridEmailClient("SG.xxxxxx"))
                .registerEmail("mailgun",  new MailgunEmailClient("key-xxxx", "mg.acme.com"))
                .registerSms("twilio", new TwilioSmsClient("ACxxxx", "authxxxx"))
                .registerPush("fcm", new FcmPushClient("/path/service-account.json"))
                .registerSlack("slack", new SlackApiClient("xoxb-***"))
                .selection(new PrimaryFallbackProviderSelection(Map.of(
                        ChannelType.EMAIL, List.of("sendgrid", "mailgun"),
                        ChannelType.SMS,   List.of("twilio"),
                        ChannelType.PUSH,  List.of("fcm"),
                        ChannelType.SLACK, List.of("slack"))))
                .retry(NotificationConfigBuilder.fixedRetry(3, 200))
                .async(NotificationConfigBuilder.async(Executors.newFixedThreadPool(4)))
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        var email = new DefaultEmailNotification(
                new DefaultRecipient("user@example.com", "User"),
                new DefaultEmailContent("Welcome", "Hello User", "<b>Hello User</b>", List.of()),
                Priority.NORMAL,
                Map.of()
        );

        var sms = new DefaultSmsNotification(
                new DefaultRecipient("+56911111111", "Ada"),
                new DefaultSmsContent("Your code is 123456",
                        com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding.GSM_7BIT, 160),
                Priority.HIGH,
                Map.of()
        );

        var push = new DefaultPushNotification(
                new DefaultRecipient("fcm_device_token_123", "Device Ada"),
                new DefaultPushContent("Hi!", "You have a message", Map.of("screen", "inbox")),
                Priority.NORMAL,
                Map.of()
        );

        var slack = new DefaultSlackNotification(
                new DefaultRecipient("C024BE7LH", "alerts-channel"),
                new DefaultSlackContent("Build succeeded :tada:", List.of()),
                Priority.LOW,
                Map.of()
        );

        facade.send(email);
        facade.sendAsync(sms).join();
        facade.send(push);
        facade.send(slack);
        System.out.println("All example notifications processed.");
    }
}