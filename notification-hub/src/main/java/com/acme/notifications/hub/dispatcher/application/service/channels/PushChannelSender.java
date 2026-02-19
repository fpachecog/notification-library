package com.acme.notifications.hub.dispatcher.application.service.channels;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.port.PushProviderClient;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.PushContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public final class PushChannelSender implements ChannelSender {
    private final NotificationConfig config;
    private final ProviderSelectionStrategy strategy;
    public PushChannelSender(NotificationConfig config) {
        this.config = config; this.strategy = config.providerSelectionStrategy();
    }
    @Override public ChannelType channel() { return ChannelType.PUSH; }
    @Override public SendResult send(Notification notification) {
        PushNotification n = (PushNotification) notification;
        PushContent c = n.content();
        var providers = config.pushProviders();
        String chosen = strategy.selectProvider(ChannelType.PUSH, providers.keySet().stream().toList());
        PushProviderClient client = providers.get(chosen);
        // FCM HTTP v1: token + notification + data  [4](https://firebase.google.com/docs/cloud-messaging/send/v1-api)
        return client.sendPush(n.recipient().id(), c.title(), c.body(), c.data());
    }
}