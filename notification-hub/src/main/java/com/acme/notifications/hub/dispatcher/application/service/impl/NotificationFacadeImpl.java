package com.acme.notifications.hub.dispatcher.application.service.impl;

import com.acme.notifications.hub.dispatcher.application.config.NotificationConfig;
import com.acme.notifications.hub.dispatcher.application.service.channels.*;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import com.acme.notifications.hub.dispatcher.core.model.api.Notification;
import com.acme.notifications.hub.dispatcher.core.service.NotificationValidator;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.application.validator.impl.PerChannelNotificationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class NotificationFacadeImpl implements NotificationFacade {
    private static final Logger log = LoggerFactory.getLogger(NotificationFacadeImpl.class);

    private final NotificationConfig config;
    private final NotificationValidator validator = new PerChannelNotificationValidator();

    private final Map<ChannelType, ChannelSender> senders = new ConcurrentHashMap<>();

    public NotificationFacadeImpl(NotificationConfig config) {
        this.config = Objects.requireNonNull(config);
        // registrar senders por canal
        this.senders.put(ChannelType.EMAIL, new EmailChannelSender(config));
        this.senders.put(ChannelType.SMS,   new SmsChannelSender(config));
        this.senders.put(ChannelType.PUSH,  new PushChannelSender(config));
        this.senders.put(ChannelType.SLACK, new SlackChannelSender(config));
    }

    @Override public SendResult send(Notification n) {
        validator.validate(n);
        ChannelSender sender = Optional.ofNullable(senders.get(n.channel()))
                .orElseThrow(() -> new IllegalStateException("No sender configured for channel: " + n.channel()));
        return doWithRetry(() -> sender.send(n));
    }

    @Override public List<SendResult> sendBatch(List<Notification> list) {
        return list.stream().map(this::send).toList();
    }

    @Override public CompletableFuture<SendResult> sendAsync(Notification n) {
        var async = config.asyncConfig();
        if (async != null && async.enabled()) {
            return CompletableFuture.supplyAsync(() -> send(n), async.executor());
        }
        return CompletableFuture.completedFuture(send(n));
    }

    private SendResult doWithRetry(SendOp op) {
        var policy = config.retryPolicy();
        int max = policy != null ? policy.maxAttempts() : 1;
        long backoff = policy != null ? policy.backoffMillis() : 0;
        RuntimeException last = null;
        for (int i=1; i<=max; i++) {
            try { return op.exec(); }
            catch (RuntimeException ex) {
                last = ex; log.warn("Attempt {}/{} failed: {}", i, max, ex.getMessage());
                if (i < max && backoff > 0) try { Thread.sleep(backoff); } catch (InterruptedException ignored) {}
            }
        }
        throw last != null ? last : new RuntimeException("Unknown failure");
    }
    @FunctionalInterface private interface SendOp { SendResult exec(); }
}