package com.acme.notifications.hub.dispatcher.core.model.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import com.acme.notifications.hub.dispatcher.core.model.api.Priority;
import com.acme.notifications.hub.dispatcher.core.model.api.Recipient;
import com.acme.notifications.hub.dispatcher.core.model.api.SmsNotification;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsContent;
import java.util.Map;

public record DefaultSmsNotification(
        Recipient recipient,
        SmsContent content,
        Priority priority,
        Map<String, Object> metadata
) implements SmsNotification {
    @Override public ChannelType channel() { return ChannelType.SMS; }
}