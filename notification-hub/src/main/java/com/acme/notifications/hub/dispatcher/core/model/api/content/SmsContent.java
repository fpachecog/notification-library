package com.acme.notifications.hub.dispatcher.core.model.api.content;

public interface SmsContent {
    String body();
    SmsEncoding encoding();
    int maxLength();
    int length();
}
