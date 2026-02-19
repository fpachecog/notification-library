package com.acme.notifications.demo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record SendPushRequest(
        @NotBlank String deviceToken,
        String title,
        @NotBlank String body,
        Map<String,String> data
) {}

