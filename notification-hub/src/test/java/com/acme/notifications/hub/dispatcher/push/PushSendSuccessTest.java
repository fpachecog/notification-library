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
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class PushSendSuccessTest {

    @Test
    void push_send_success_returns_success_result() {
        PushProviderClient push = mock(PushProviderClient.class);
        when(push.sendPush(any(), any(), any(), any()))
                .thenReturn(new SendResult() {
                    public boolean success() { return true; }
                    public String provider() { return "mock-fcm"; }
                    public String providerMessageId() { return "fcm-1"; }
                    public String errorCode() { return null; }
                    public String errorMessage() { return null; }
                });

        NotificationConfig cfg = new NotificationConfigBuilder()
                .registerPush("fcm", push)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.PUSH, List.of("fcm"))))
                .retry(NotificationConfigBuilder.fixedRetry(1, 0))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        PushNotification n = new DefaultPushNotification(
                new DefaultRecipient("device_token_abc", "Device"),
                new DefaultPushContent("Hi!", "Body", Map.of("k","v")),
                Priority.NORMAL,
                Map.of()
        );

        SendResult result = facade.send(n);

        assertTrue(result.success());
        assertEquals("mock-fcm", result.provider());
        assertEquals("fcm-1", result.providerMessageId());
        verify(push, times(1)).sendPush(any(), any(), any(), any());
    }
}
