package com.acme.notifications.hub.dispatcher.core.model.api;

import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsContent;

public interface SmsNotification extends Notification {
    SmsContent content();
}