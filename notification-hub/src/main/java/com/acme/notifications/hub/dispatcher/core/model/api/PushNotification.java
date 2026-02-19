package com.acme.notifications.hub.dispatcher.core.model.api;

import com.acme.notifications.hub.dispatcher.core.model.api.content.PushContent;

public interface PushNotification extends Notification {
    PushContent content();
}