package com.acme.notifications.demo.controller;

import com.acme.notifications.demo.dto.SendPushRequest;
import com.acme.notifications.demo.dto.SendResponse;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultPushContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/push")
public class PushController {
    private final NotificationFacade facade;
    public PushController(NotificationFacade facade) { this.facade = facade; }

    @PostMapping
    public ResponseEntity<SendResponse> send(@Valid @RequestBody SendPushRequest req) {
        var notification = new DefaultPushNotification(
                new DefaultRecipient(req.deviceToken(), "device"),
                new DefaultPushContent(req.title(), req.body(), req.data()),
                Priority.NORMAL,
                java.util.Map.of()
        );
        SendResult r = facade.send(notification);
        return ResponseEntity.ok(new SendResponse(r.success(), r.provider(), r.providerMessageId(), r.errorCode(), r.errorMessage()));
    }
}