package com.acme.notifications.hub.dispatcher.infrastructure.client.push;

import com.acme.notifications.hub.dispatcher.application.port.PushProviderClient;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class FcmPushClient implements PushProviderClient {
    private static final Logger log = LoggerFactory.getLogger(FcmPushClient.class);
    private final String serviceAccountJsonPath;
    public FcmPushClient(String serviceAccountJsonPath) { this.serviceAccountJsonPath = serviceAccountJsonPath; }

    @Override public SendResult sendPush(String deviceToken, String title, String body, Map<String, String> data) {
        log.info("[FCM] Simulate PUSH: token={}, title={}, dataKeys={}", deviceToken, title, data != null ? data.keySet() : "none");
        return new SimulatedSendResult(true, "fcm", "fcm-321", null, null);
    }
}