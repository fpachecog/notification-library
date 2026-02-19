package com.acme.notifications.hub.dispatcher.email;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.port.EmailProviderClient;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultEmailNotification;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultRecipient;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultEmailContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class EmailSendSuccessTest {

    @Test
    void email_send_success_returns_success_result() {
        EmailProviderClient provider = mock(EmailProviderClient.class);
        when(provider.sendEmail(any(), any(), any(), any(), any()))
                .thenReturn(new SendResult() {
                    public boolean success() { return true; }
                    public String provider() { return "mock-mail"; }
                    public String providerMessageId() { return "id-1"; }
                    public String errorCode() { return null; }
                    public String errorMessage() { return null; }
                });

        NotificationConfig cfg = new NotificationConfigBuilder()
                .defaultFromEmail("noreply@acme.com")
                .registerEmail("mockMail", provider)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.EMAIL, List.of("mockMail"))))
                .retry(NotificationConfigBuilder.fixedRetry(1, 0))
                .async(new NotificationConfig.AsyncConfig() { public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        EmailNotification email = new DefaultEmailNotification(
                new DefaultRecipient("dev@example.com", "Dev"),
                new DefaultEmailContent("Hello", "Text", "<b>Text</b>", List.of()),
                Priority.NORMAL,
                Map.of()
        );

        SendResult result = facade.send(email);

        assertTrue(result.success());
        assertEquals("mock-mail", result.provider());
        assertEquals("id-1", result.providerMessageId());
        verify(provider, times(1)).sendEmail(any(), any(), any(), any(), any());
    }
}