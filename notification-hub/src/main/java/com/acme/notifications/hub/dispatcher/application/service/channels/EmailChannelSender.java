package com.acme.notifications.hub.dispatcher.application.service.channels;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.port.EmailProviderClient;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;

import java.util.List;

public final class EmailChannelSender implements ChannelSender {
    private final NotificationConfig config;
    private final ProviderSelectionStrategy strategy;

    public EmailChannelSender(NotificationConfig config) {
        this.config = config;
        this.strategy = config.providerSelectionStrategy();
    }

    @Override public ChannelType channel() { return ChannelType.EMAIL; }

    @Override public SendResult send(Notification notification) {
        EmailNotification n = (EmailNotification) notification;
        EmailContent c = n.content();
        var providers = config.emailProviders();
        String chosen = strategy.selectProvider(ChannelType.EMAIL, providers.keySet().stream().toList());
        EmailProviderClient client = providers.get(chosen);
        if (c.attachments() != null && !c.attachments().isEmpty()) {
            return client.sendEmailWithAttachments(
                    config.defaultFromEmail(), n.recipient().id(), c.subject(), c.textBody(), c.htmlBody(), c.attachments()
            );
        }
        return client.sendEmail(config.defaultFromEmail(), n.recipient().id(), c.subject(), c.textBody(), c.htmlBody());
    }
}
