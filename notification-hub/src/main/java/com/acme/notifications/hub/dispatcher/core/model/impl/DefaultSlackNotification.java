package com.acme.notifications.hub.dispatcher.core.model.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SlackContent;
import java.util.Map;

public record DefaultSlackNotification(
        Recipient recipient,
        SlackContent content,
        Priority priority,
        Map<String, Object> metadata
) implements SlackNotification {
    @Override public ChannelType channel() { return ChannelType.SLACK; }
}