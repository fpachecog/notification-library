package com.acme.notifications.hub.dispatcher.core.model.api.content;

public interface EmailAttachment {
    String filename();
    String contentType();
    byte[] data();
}