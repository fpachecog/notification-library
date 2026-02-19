package com.acme.notifications.hub.dispatcher.infrastructure.client.email;

import com.acme.notifications.hub.dispatcher.application.port.EmailProviderClient;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class SendGridEmailClient implements EmailProviderClient {
    private static final Logger log = LoggerFactory.getLogger(SendGridEmailClient.class);
    private final String apiKey;
    public SendGridEmailClient(String apiKey) { this.apiKey = apiKey; }
    @Override public SendResult sendEmail(String from, String to, String subject, String textBody, String htmlBody) {
        log.info("[SendGrid] Simulate send: to={}, subject={}", to, subject);
        return new SimulatedSendResult(true, "sendgrid", "sg-123", null, null);
    }
    @Override public SendResult sendEmailWithAttachments(String from, String to, String subject, String textBody, String htmlBody, List<EmailAttachment> attachments) {
        log.info("[SendGrid] Simulate send with {} attachment(s): to={}, subject={}", attachments.size(), to, subject);
        return new SimulatedSendResult(true, "sendgrid", "sg-123-att", null, null);
    }
}