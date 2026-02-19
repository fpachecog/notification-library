package com.acme.notifications.demo.controller;

import com.acme.notifications.demo.dto.*;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.*;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications/email")
public class EmailController {

    private final NotificationFacade facade;

    public EmailController(NotificationFacade facade) { this.facade = facade; }

    @PostMapping
    public ResponseEntity<SendResponse> send(@Valid @RequestBody SendEmailRequest req) {
        var notification = new DefaultEmailNotification(
                new DefaultRecipient(req.to(), null),
                new DefaultEmailContent(
                        req.subject(),
                        req.textBody(),
                        req.htmlBody(),
                        List.of()
                ),
                Priority.NORMAL,
                req.metadata()
        );
        SendResult r = facade.send(notification);
        return ResponseEntity.ok(new SendResponse(r.success(), r.provider(), r.providerMessageId(), r.errorCode(), r.errorMessage()));
    }
}