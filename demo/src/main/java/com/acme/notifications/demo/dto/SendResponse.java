package com.acme.notifications.demo.dto;

public record SendResponse(
        boolean success,
        String provider,
        String providerMessageId,
        String errorCode,
        String errorMessage
) {}