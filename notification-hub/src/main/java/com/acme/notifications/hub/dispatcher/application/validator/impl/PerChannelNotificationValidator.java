package com.acme.notifications.hub.dispatcher.application.validator.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding;
import com.acme.notifications.hub.dispatcher.core.service.NotificationValidator;

import java.util.regex.Pattern;

public final class PerChannelNotificationValidator implements NotificationValidator {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern E164  = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    private static final Pattern SLACK_CHANNEL = Pattern.compile("^[CDGU][A-Z0-9]{8,}$"); // simple check

    @Override
    public void validate(Notification n) {
        if (n == null) throw new IllegalArgumentException("Notification is null");
        if (n.recipient() == null || n.recipient().id() == null || n.recipient().id().isBlank())
            throw new IllegalArgumentException("Recipient is required");

        switch (n.channel()) {
            case EMAIL -> {
                EmailNotification en = (EmailNotification) n;
                if (!EMAIL.matcher(en.recipient().id()).matches())
                    throw new IllegalArgumentException("Invalid email address");
                if (en.content() == null || en.content().subject() == null || en.content().subject().isBlank())
                    throw new IllegalArgumentException("Email subject is required");
            }
            case SMS -> {
                SmsNotification sn = (SmsNotification) n;
                if (!E164.matcher(sn.recipient().id()).matches())
                    throw new IllegalArgumentException("Invalid E.164 phone");
                var c = sn.content();
                if (c.encoding() == SmsEncoding.GSM_7BIT && c.length() > c.maxLength())
                    throw new IllegalArgumentException("SMS body exceeds GSM-7 single message length");
            }
            case PUSH -> {
                if (n.recipient().id().isBlank())
                    throw new IllegalArgumentException("Device token is required");
            }
            case SLACK -> {
                if (!SLACK_CHANNEL.matcher(n.recipient().id()).matches())
                    throw new IllegalArgumentException("Slack channel/user id format is invalid");
            }
        }
    }
}