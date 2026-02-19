package com.acme.notifications.hub.dispatcher.core.model.impl.content;

import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailContent;
import java.util.List;

public record DefaultEmailContent(String subject, String textBody, String htmlBody, List<EmailAttachment> attachments)
        implements EmailContent { }