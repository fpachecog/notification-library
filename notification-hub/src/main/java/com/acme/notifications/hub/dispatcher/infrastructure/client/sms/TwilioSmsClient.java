package com.acme.notifications.hub.dispatcher.infrastructure.client.sms;

import com.acme.notifications.hub.dispatcher.application.port.SmsProviderClient;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TwilioSmsClient implements SmsProviderClient {
    private static final Logger log = LoggerFactory.getLogger(TwilioSmsClient.class);
    private final String accountSid; private final String authToken;
    public TwilioSmsClient(String accountSid, String authToken) {
        this.accountSid = accountSid; this.authToken = authToken;
    }
    @Override public SendResult sendSms(String from, String to, String body) {
        log.info("[Twilio] Simulate SMS: to={}, from={}, body={}", to, from, body);
        return new SimulatedSendResult(true, "twilio", "tw-789", null, null);
    }
}