package com.acme.notifications.hub.dispatcher.infrastructure.client;

import com.acme.notifications.hub.dispatcher.core.service.SendResult;

public record SimulatedSendResult(
        boolean success,
        String provider,
        String providerMessageId,
        String errorCode,
        String errorMessage
) implements SendResult { }