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
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class SmsSendSuccessTest {

    @Test
    void sms_send_success_returns_success_result() {
        SmsProviderClient sms = mock(SmsProviderClient.class);
        when(sms.sendSms(any(), any(), any()))
                .thenReturn(new SendResult() {
                    public boolean success() { return true; }
                    public String provider() { return "mock-sms"; }
                    public String providerMessageId() { return "sms-1"; }
                    public String errorCode() { return null; }
                    public String errorMessage() { return null; }
                });

        NotificationConfig cfg = new NotificationConfigBuilder()
                .defaultFromSms("+12345678900")
                .registerSms("twilio", sms)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.SMS, List.of("twilio"))))
                .retry(NotificationConfigBuilder.fixedRetry(1, 0))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        SmsNotification n = new DefaultSmsNotification(
                new DefaultRecipient("+56911111111", "Ada"),
                new DefaultSmsContent("Your code is 123456", SmsEncoding.GSM_7BIT, 160),
                Priority.HIGH,
                Map.of()
        );

        SendResult result = facade.send(n);

        assertTrue(result.success());
        assertEquals("mock-sms", result.provider());
        assertEquals("sms-1", result.providerMessageId());
        verify(sms, times(1)).sendSms(any(), any(), any());
    }
}