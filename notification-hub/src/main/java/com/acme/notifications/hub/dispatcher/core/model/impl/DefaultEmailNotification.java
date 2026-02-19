package com.acme.notifications.hub.dispatcher.core.model.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import com.acme.notifications.hub.dispatcher.core.model.api.EmailNotification;
import com.acme.notifications.hub.dispatcher.core.model.api.Priority;
import com.acme.notifications.hub.dispatcher.core.model.api.Recipient;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailContent;
import java.util.Map;

public record DefaultEmailNotification(
        Recipient recipient,
        EmailContent content,
        Priority priority,
        Map<String, Object> metadata
) implements EmailNotification {
    @Override public ChannelType channel() { return ChannelType.EMAIL; }
}