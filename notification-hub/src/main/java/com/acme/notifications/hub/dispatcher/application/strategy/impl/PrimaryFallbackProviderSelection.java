package com.acme.notifications.hub.dispatcher.application.strategy.impl;

import com.acme.notifications.hub.dispatcher.application.strategy.ProviderSelectionStrategy;
import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;

import java.util.List;
import java.util.Map;

public final class PrimaryFallbackProviderSelection implements ProviderSelectionStrategy {
    private final Map<ChannelType, List<String>> priorities;
    public PrimaryFallbackProviderSelection(Map<ChannelType, List<String>> priorities) {
        this.priorities = Map.copyOf(priorities);
    }
    @Override
    public String selectProvider(ChannelType channel, List<String> availableProviders) {
        for (String preferred : priorities.getOrDefault(channel, List.of())) {
            if (availableProviders.contains(preferred)) return preferred;
        }
        return availableProviders.isEmpty() ? null : availableProviders.get(0);
    }
}