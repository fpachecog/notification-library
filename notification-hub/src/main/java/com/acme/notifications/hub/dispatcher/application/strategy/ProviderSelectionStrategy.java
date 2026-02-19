package com.acme.notifications.hub.dispatcher.application.strategy;

import com.acme.notifications.hub.dispatcher.core.model.api.ChannelType;
import java.util.List;

public interface ProviderSelectionStrategy {
    String selectProvider(ChannelType channel, List<String> availableProviders);
}