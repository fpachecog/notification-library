package com.acme.notifications.hub.dispatcher.core.model.api;

import com.acme.notifications.hub.dispatcher.core.model.api.content.SlackContent;

public interface SlackNotification extends Notification {
    SlackContent content();
}