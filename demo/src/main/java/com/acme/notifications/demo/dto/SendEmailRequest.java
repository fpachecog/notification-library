package com.acme.notifications.demo.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public record SendEmailRequest(
        @NotBlank String to,
        @NotBlank String subject,
        String textBody,
        String htmlBody,
        List<EmailAttachmentDto> attachments,
        Map<String,Object> metadata
) {}
