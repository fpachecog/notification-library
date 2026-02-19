package com.acme.notifications.hub.dispatcher.infrastructure.client.email;

import com.acme.notifications.hub.dispatcher.application.port.EmailProviderClient;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class MailgunEmailClient implements EmailProviderClient {
    private static final Logger log = LoggerFactory.getLogger(MailgunEmailClient.class);
    private final String apiKey; private final String domain;
    public MailgunEmailClient(String apiKey, String domain) { this.apiKey=apiKey; this.domain=domain; }

    @Override public SendResult sendEmail(String from, String to, String subject, String textBody, String htmlBody) {
        log.info("[Mailgun] Simulate send: domain={}, to={}, subject={}", domain, to, subject);
        return new SimulatedSendResult(true, "mailgun", "mg-456", null, null);
    }

    @Override public SendResult sendEmailWithAttachments(String from, String to, String subject, String textBody, String htmlBody, List<EmailAttachment> attachments) {
        log.info("[Mailgun] Simulate send with {} attachment(s): domain={}, to={}, subject={}", attachments.size(), domain, to, subject);
        return new SimulatedSendResult(true, "mailgun", "mg-456-att", null, null);
    }
}