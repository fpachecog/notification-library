package com.acme.notifications.hub.dispatcher.application.port;

import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import java.util.Map;

public interface PushProviderClient {
    SendResult sendPush(String deviceToken, String title, String body, Map<String, String> data);
}