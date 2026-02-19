package com.acme.notifications.hub.dispatcher.slack;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.config.impl.NotificationConfigBuilder;
import com.acme.notifications.hub.dispatcher.application.port.SlackProviderClient;
import com.acme.notifications.hub.dispatcher.application.service.impl.NotificationFacadeImpl;
import com.acme.notifications.hub.dispatcher.application.strategy.impl.PrimaryFallbackProviderSelection;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultRecipient;
import com.acme.notifications.hub.dispatcher.core.model.impl.DefaultSlackNotification;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultSlackContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class SlackSendSuccessTest {

    @Test
    void slack_send_success_returns_success_result() {
        SlackProviderClient slack = mock(SlackProviderClient.class);
        when(slack.postMessage(any(), any(), any()))
                .thenReturn(new SendResult() {
                    public boolean success() { return true; }
                    public String provider() { return "mock-slack"; }
                    public String providerMessageId() { return "sl-1"; }
                    public String errorCode() { return null; }
                    public String errorMessage() { return null; }
                });

        NotificationConfig cfg = new NotificationConfigBuilder()
                .registerSlack("slack", slack)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.SLACK, List.of("slack"))))
                .retry(NotificationConfigBuilder.fixedRetry(1, 0))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        SlackNotification n = new DefaultSlackNotification(
                new DefaultRecipient("C024BE7LH", "alerts"),
                new DefaultSlackContent("Deploy ok", List.of()),
                Priority.LOW,
                Map.of()
        );

        SendResult result = facade.send(n);

        assertTrue(result.success());
        assertEquals("mock-slack", result.provider());
        assertEquals("sl-1", result.providerMessageId());
        verify(slack, times(1)).postMessage(any(), any(), any());
    }
}