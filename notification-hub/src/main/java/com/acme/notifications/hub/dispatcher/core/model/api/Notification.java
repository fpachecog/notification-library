package com.acme.notifications.hub.dispatcher.core.model.api;

import java.util.Map;

public interface Notification {
    ChannelType channel();
    Recipient recipient();
    Priority priority();
    Map<String, Object> metadata();
}

