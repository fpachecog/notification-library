package com.acme.notifications.hub.dispatcher.core.model.api.content;

import java.util.List;

public interface EmailContent {
    String subject();
    String textBody();   // plain text
    String htmlBody();   // optional
    List<EmailAttachment> attachments();
}