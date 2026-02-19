package com.acme.notifications.hub.dispatcher.core.facade;

import com.acme.notifications.hub.dispatcher.core.model.api.Notification;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationFacade {
    SendResult send(Notification notification);
    List<SendResult> sendBatch(List<Notification> notifications);
    CompletableFuture<SendResult> sendAsync(Notification notification);
}