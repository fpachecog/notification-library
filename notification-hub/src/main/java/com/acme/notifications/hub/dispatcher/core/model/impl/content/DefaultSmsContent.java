package com.acme.notifications.hub.dispatcher.core.model.impl.content;

import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsContent;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding;

public record DefaultSmsContent(String body, SmsEncoding encoding, int maxLength) implements SmsContent {
    @Override public int length() {
        return body == null ? 0 : body.length();
    }
}