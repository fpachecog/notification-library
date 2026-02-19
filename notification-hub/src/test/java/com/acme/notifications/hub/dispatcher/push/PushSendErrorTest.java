package com.acme.notifications.hub.dispatcher.push;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.port.PushProviderClient;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultPushNotification;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultRecipient;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultPushContent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class PushSendErrorTest {

    @Test
    void push_send_retries_and_throws_when_no_fallback() {
        PushProviderClient push = mock(PushProviderClient.class);
        when(push.sendPush(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("fcm down"));

        NotificationConfig cfg = new NotificationConfigBuilder()
                .registerPush("fcm", push)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.PUSH, List.of("fcm"))))
                .retry(NotificationConfigBuilder.fixedRetry(2, 1))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        PushNotification n = new DefaultPushNotification(
                new DefaultRecipient("device_token_abc", "Device"),
                new DefaultPushContent("Hi!", "Body", Map.of("k","v")),
                Priority.NORMAL,
                Map.of()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> facade.send(n));
        assertTrue(ex.getMessage().contains("fcm down"));
        verify(push, times(2)).sendPush(any(), any(), any(), any());
    }
}