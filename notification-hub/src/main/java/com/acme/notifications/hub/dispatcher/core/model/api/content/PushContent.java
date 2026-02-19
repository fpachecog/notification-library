package com.acme.notifications.hub.dispatcher.core.model.api.content;

import java.util.Map;

public interface PushContent {
    String title();
    String body();
    Map<String, String> data(); // custom payload
}