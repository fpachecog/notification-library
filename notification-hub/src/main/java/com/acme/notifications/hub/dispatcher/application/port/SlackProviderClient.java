package com.acme.notifications.hub.dispatcher.application.port;

import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import java.util.List;
import java.util.Map;

public interface SlackProviderClient {
    SendResult postMessage(String channel, String text, List<Map<String, Object>> blocks);
}