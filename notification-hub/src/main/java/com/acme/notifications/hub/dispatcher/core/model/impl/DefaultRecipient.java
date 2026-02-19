package com.acme.notifications.hub.dispatcher.core.model.impl;

import com.acme.notifications.hub.dispatcher.core.model.api.Recipient;

public record DefaultRecipient(String id, String displayName) implements Recipient { }