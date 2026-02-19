package com.acme.notifications.hub.dispatcher.infrastructure.client.slack;

import com.acme.notifications.hub.dispatcher.application.port.SlackProviderClient;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class SlackApiClient implements SlackProviderClient {
    private static final Logger log = LoggerFactory.getLogger(SlackApiClient.class);
    private final String botToken;
    public SlackApiClient(String botToken) { this.botToken = botToken; }

    @Override public SendResult postMessage(String channel, String text, List<Map<String, Object>> blocks) {
        log.info("[Slack] Simulate post: channel={}, text={}, blocks={}", channel, text, blocks != null ? blocks.size() : 0);
        return new SimulatedSendResult(true, "slack", "slack-654", null, null);
    }
}