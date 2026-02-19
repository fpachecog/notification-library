package com.acme.notifications.hub.dispatcher.application.service.channels;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.port.SlackProviderClient;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SlackContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public final class SlackChannelSender implements ChannelSender {
    private final NotificationConfig config;
    private final ProviderSelectionStrategy strategy;
    public SlackChannelSender(NotificationConfig config) {
        this.config = config; this.strategy = config.providerSelectionStrategy();
    }
    @Override public ChannelType channel() { return ChannelType.SLACK; }
    @Override public SendResult send(Notification notification) {
        SlackNotification n = (SlackNotification) notification;
        SlackContent c = n.content();
        var providers = config.slackProviders();
        String chosen = strategy.selectProvider(ChannelType.SLACK, providers.keySet().stream().toList());
        SlackProviderClient client = providers.get(chosen);
        // chat.postMessage: channel + text/blocks  [5](https://docs.slack.dev/reference/methods/chat.postMessage/)
        return client.postMessage(n.recipient().id(), c.text(), c.blocks());
    }
}