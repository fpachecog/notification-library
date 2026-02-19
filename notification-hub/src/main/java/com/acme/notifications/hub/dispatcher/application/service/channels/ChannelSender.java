package com.acme.notifications.hub.dispatcher.application.service.channels;

import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import com.acme.notifications.hub.dispatcher.core.model.api.Notification;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public interface ChannelSender {
    ChannelType channel();
    SendResult send(Notification notification);
}