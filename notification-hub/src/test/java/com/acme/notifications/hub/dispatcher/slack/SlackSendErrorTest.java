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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class SlackSendErrorTest {

    @Test
    void slack_send_retries_and_throws_when_no_fallback() {
        SlackProviderClient slack = mock(SlackProviderClient.class);
        when(slack.postMessage(any(), any(), any()))
                .thenThrow(new RuntimeException("slack down"));

        NotificationConfig cfg = new NotificationConfigBuilder()
                .registerSlack("slack", slack)
                .selection(new PrimaryFallbackProviderSelection(Map.of(ChannelType.SLACK, List.of("slack"))))
                .retry(NotificationConfigBuilder.fixedRetry(2, 1))
                .async(new NotificationConfig.AsyncConfig(){ public boolean enabled(){return false;} public java.util.concurrent.Executor executor(){return null;} })
                .build();

        NotificationFacade facade = new NotificationFacadeImpl(cfg);

        SlackNotification n = new DefaultSlackNotification(
                new DefaultRecipient("C024BE7LH", "alerts"),
                new DefaultSlackContent("Deploy ok", List.of()),
                Priority.LOW,
                Map.of()
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> facade.send(n));
        assertTrue(ex.getMessage().contains("slack down"));
        verify(slack, times(2)).postMessage(any(), any(), any());
    }
}