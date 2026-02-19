package com.acme.notifications.hub.dispatcher.application.port;

import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import java.util.List;

public interface EmailProviderClient {
    SendResult sendEmail(String from, String to, String subject, String textBody, String htmlBody);
    default SendResult sendEmailWithAttachments(String from, String to, String subject, String textBody, String htmlBody,
                                                List<EmailAttachment> attachments) {

        return sendEmail(from, to, subject, textBody, htmlBody);
    }
}