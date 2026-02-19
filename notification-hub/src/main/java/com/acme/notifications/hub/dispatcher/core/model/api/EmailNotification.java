package com.acme.notifications.hub.dispatcher.core.model.api;

import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailContent;

public interface EmailNotification extends Notification {
    EmailContent content();
}