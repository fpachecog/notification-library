package com.acme.notifications.hub.dispatcher.core.service;

public interface SendResult {
    boolean success();
    String provider();
    String providerMessageId();
    String errorCode();
    String errorMessage();
}