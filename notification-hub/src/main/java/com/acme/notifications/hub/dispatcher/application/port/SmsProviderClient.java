package com.acme.notifications.hub.dispatcher.application.port;

import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public interface SmsProviderClient {
    SendResult sendSms(String from, String to, String body);
}