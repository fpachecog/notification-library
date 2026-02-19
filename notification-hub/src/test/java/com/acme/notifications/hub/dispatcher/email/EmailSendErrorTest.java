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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class EmailSendErrorTest {

    @Test
    void email_send_retries_and_throws_when_no_fallback() {
        EmailProviderClient provider = mock(EmailProviderClient.class);
        when(provider.sendEmail(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("provider down"));

        NotificationConfig cfg = new NotificationConfigBuilder()
                .defaultFromEmail("noreply@acme.com")
                .registerEmail("primary", provider)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.EMAIL, List.of("primary"))))
                .retry(NotificationConfigBuilder.fixedRetry(2, 1))
                .async(new NotificationConfig.AsyncConfig() { public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        EmailNotification n = new DefaultEmailNotification(
                new DefaultRecipient("dev@example.com", "Dev"),
                new DefaultEmailContent("Hello", "Text", "<b>Text</b>", List.of()),
                Priority.NORMAL,
                Map.of()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> facade.send(n));
        assertTrue(ex.getMessage().contains("provider down"));
        verify(provider, times(2)).sendEmail(any(), any(), any(), any(), any());
    }
}