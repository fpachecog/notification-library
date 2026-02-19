package com.acme.notifications.demo.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

public record SendSlackRequest(
        @NotBlank String channelId,
        String text,
        List<Map<String,Object>> blocks
) {}
