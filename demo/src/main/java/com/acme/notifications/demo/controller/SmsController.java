package com.acme.notifications.demo.controller;

import com.acme.notifications.demo.dto.SendResponse;
import com.acme.notifications.demo.dto.SendSmsRequest;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding;
import com.acme.notifications.hub.dispatcher.core.model.impl.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultSmsContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/sms")
public class SmsController {
    private final NotificationFacade facade;
    public SmsController(NotificationFacade facade) { this.facade = facade; }

    @PostMapping
    public ResponseEntity<SendResponse> send(@Valid @RequestBody SendSmsRequest req) {
        var notification = new DefaultSmsNotification(
                new DefaultRecipient(req.toE164(), null),
                new DefaultSmsContent(req.body(), SmsEncoding.GSM_7BIT, 160),
                Priority.HIGH,
                java.util.Map.of()
        );
        SendResult r = facade.send(notification);
        return ResponseEntity.ok(new SendResponse(r.success(), r.provider(), r.providerMessageId(), r.errorCode(), r.errorMessage()));
    }
}