package com.acme.notifications.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailAttachmentDto(
        @NotBlank String filename,
        @NotBlank String contentType,
        @NotBlank String base64
) {}
