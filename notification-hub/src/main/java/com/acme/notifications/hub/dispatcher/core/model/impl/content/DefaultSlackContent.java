package com.acme.notifications.hub.dispatcher.core.model.impl.content;

import com.acme.notifications.hub.dispatcher.core.model.api.content.SlackContent;
import java.util.List; import java.util.Map;

public record DefaultSlackContent(String text, List<Map<String, Object>> blocks)
        implements SlackContent { }