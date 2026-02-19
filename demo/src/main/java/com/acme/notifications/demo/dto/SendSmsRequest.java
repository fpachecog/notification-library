package com.acme.notifications.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record SendSmsRequest(
        @NotBlank String toE164,
        @NotBlank String body
) {}
