package com.acme.notifications.hub.dispatcher.core.service;

import com.acme.notifications.hub.dispatcher.core.model.api.Notification;

public interface NotificationValidator {
    void validate(Notification notification);
}