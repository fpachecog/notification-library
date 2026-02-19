package com.acme.notifications.hub.dispatcher.core.model.api.content;
import java.util.List;
import java.util.Map;

public interface SlackContent {
    String text();
    List<Map<String, Object>> blocks();
}