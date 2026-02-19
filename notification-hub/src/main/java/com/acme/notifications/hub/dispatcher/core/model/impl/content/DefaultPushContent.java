package com.acme.notifications.hub.dispatcher.core.model.impl.content;

import com.acme.notifications.hub.dispatcher.core.model.api.content.PushContent;
import java.util.Map;

public record DefaultPushContent(String title, String body, Map<String, String> data)
        implements PushContent {
}