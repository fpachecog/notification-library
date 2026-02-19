package com.acme.notifications.hub.dispatcher.application.config.impl;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.port.*;
import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class NotificationConfigBuilder {
    private String defaultFromEmail;
    private String defaultFromSms;

    private final Map<String, EmailProviderClient> emailProviders = new HashMap<>();
    private final Map<String, SmsProviderClient>   smsProviders   = new HashMap<>();
    private final Map<String, PushProviderClient>  pushProviders  = new HashMap<>();
    private final Map<String, SlackProviderClient> slackProviders = new HashMap<>();

    private ProviderSelectionStrategy selection;
    private NotificationConfig.RetryPolicy retryPolicy;
    private NotificationConfig.AsyncConfig asyncConfig;

    public NotificationConfigBuilder defaultFromEmail(String value) {
        this.defaultFromEmail = value; return this;
    }

    public NotificationConfigBuilder defaultFromSms(String value) {
        this.defaultFromSms = value; return this;
    }

    public NotificationConfigBuilder registerEmail(String name, EmailProviderClient client) {
        this.emailProviders.put(Objects.requireNonNull(name), Objects.requireNonNull(client)); return this;
    }

    public NotificationConfigBuilder registerSms(String name, SmsProviderClient client) {
        this.smsProviders.put(Objects.requireNonNull(name), Objects.requireNonNull(client)); return this;
    }

    public NotificationConfigBuilder registerPush(String name, PushProviderClient client) {
        this.pushProviders.put(Objects.requireNonNull(name), Objects.requireNonNull(client)); return this;
    }

    public NotificationConfigBuilder registerSlack(String name, SlackProviderClient client) {
        this.slackProviders.put(Objects.requireNonNull(name), Objects.requireNonNull(client)); return this;
    }

    public NotificationConfigBuilder selection(ProviderSelectionStrategy strategy) {
        this.selection = Objects.requireNonNull(strategy); return this;
    }

    public NotificationConfigBuilder retry(NotificationConfig.RetryPolicy policy) {
        this.retryPolicy = Objects.requireNonNull(policy); return this;
    }

    public NotificationConfigBuilder async(NotificationConfig.AsyncConfig async) {
        this.asyncConfig = Objects.requireNonNull(async); return this;
    }

    public NotificationConfig build() {
        return new NotificationConfig() {
            public String defaultFromEmail(){ return defaultFromEmail; }
            public String defaultFromSms(){ return defaultFromSms; }
            public Map<String, EmailProviderClient> emailProviders(){ return Map.copyOf(emailProviders); }
            public Map<String, SmsProviderClient>   smsProviders(){ return Map.copyOf(smsProviders); }
            public Map<String, PushProviderClient>  pushProviders(){ return Map.copyOf(pushProviders); }
            public Map<String, SlackProviderClient> slackProviders(){ return Map.copyOf(slackProviders); }
            public ProviderSelectionStrategy providerSelectionStrategy(){ return selection; }
            public RetryPolicy retryPolicy(){ return retryPolicy; }
            public AsyncConfig asyncConfig(){ return asyncConfig; }
        };
    }

    // Atajos útiles si prefieres crear policies inline
    public static NotificationConfig.RetryPolicy fixedRetry(int maxAttempts, long backoffMillis) {
        return new NotificationConfig.RetryPolicy() {
            public int maxAttempts() { return maxAttempts; }
            public long backoffMillis() { return backoffMillis; }
        };
    }

    public static NotificationConfig.AsyncConfig async(Executor executor) {
        return new NotificationConfig.AsyncConfig() {
            public boolean enabled() { return true; }
            public Executor executor() { return executor; }
        };
    }
}