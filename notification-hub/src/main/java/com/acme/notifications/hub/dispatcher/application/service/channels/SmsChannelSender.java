package com.acme.notifications.hub.dispatcher.application.service.channels;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.port.SmsProviderClient;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public final class SmsChannelSender implements ChannelSender {
    private final NotificationConfig config;
    private final ProviderSelectionStrategy strategy;
    public SmsChannelSender(NotificationConfig config) {
        this.config = config; this.strategy = config.providerSelectionStrategy();
    }
    @Override public ChannelType channel() { return ChannelType.SMS; }
    @Override public SendResult send(Notification notification) {
        SmsNotification n = (SmsNotification) notification;
        SmsContent c = n.content();
        var providers = config.smsProviders();
        String chosen = strategy.selectProvider(ChannelType.SMS, providers.keySet().stream().toList());
        SmsProviderClient client = providers.get(chosen);
        // Twilio necesita From/To/Body; el encoding/length afecta lógica de segmentación upstream  [3](https://help.twilio.com/articles/223133907-Simple-example-for-sending-SMS-or-MMS-messages)
        return client.sendSms(config.defaultFromSms(), n.recipient().id(), c.body());
    }
}