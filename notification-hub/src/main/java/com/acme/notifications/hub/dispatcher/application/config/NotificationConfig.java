package com.acme.notifications.hub.dispatcher.application.config;

import com.acme.notifications.hub.dispatcher.application.port.*;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;

import java.util.Map;
import java.util.concurrent.Executor;

public interface NotificationConfig {
    String defaultFromEmail();
    String defaultFromSms();

    Map<String, EmailProviderClient> emailProviders();
    Map<String, SmsProviderClient> smsProviders();
    Map<String, PushProviderClient> pushProviders();
    Map<String, SlackProviderClient> slackProviders();

    ProviderSelectionStrategy providerSelectionStrategy();
    RetryPolicy retryPolicy();

    AsyncConfig asyncConfig();
    interface AsyncConfig { boolean enabled(); Executor executor(); }
    interface RetryPolicy { int maxAttempts(); long backoffMillis(); }
}