package com.acme.notifications.hub.dispatcher.core.model.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.PushContent;
import java.util.Map;

public record DefaultPushNotification(
        Recipient recipient,
        PushContent content,
        Priority priority,
        Map<String, Object> metadata
) implements PushNotification {
    @Override public ChannelType channel() { return ChannelType.PUSH; }
}