package com.acme.notifications.hub.dispatcher.sms;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.port.SmsProviderClient;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultRecipient;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultSmsNotification;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultSmsContent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class SmsSendErrorTest {

    @Test
    void sms_send_retries_and_throws_when_no_fallback() {
        SmsProviderClient sms = mock(SmsProviderClient.class);
        when(sms.sendSms(any(), any(), any()))
                .thenThrow(new RuntimeException("sms provider down"));

        NotificationConfig cfg = new NotificationConfigBuilder()
                .defaultFromSms("+12345678900")
                .registerSms("twilio", sms)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.SMS, List.of("twilio"))))
                .retry(NotificationConfigBuilder.fixedRetry(2, 1))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        SmsNotification n = new DefaultSmsNotification(
                new DefaultRecipient("+56911111111", "Ada"),
                new DefaultSmsContent("Hi", SmsEncoding.GSM_7BIT, 160),
                Priority.NORMAL,
                Map.of()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> facade.send(n));
        assertTrue(ex.getMessage().contains("sms provider down"));
        verify(sms, times(2)).sendSms(any(), any(), any());
    }
}