package com.acme.notifications.hub.dispatcher.core.model.impl.content;

import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;

public record DefaultEmailAttachment(String filename, String contentType, byte[] data)
        implements EmailAttachment { }