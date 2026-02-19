package com.acme.notifications.demo.controller;

import com.acme.notifications.demo.dto.SendResponse;
import com.acme.notifications.demo.dto.SendSlackRequest;
import com.acme.notifications.hub.dispatcher.core.facade.NotificationFacade;
import com.acme.notifications.hub.dispatcher.core.model.api.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.*;
import com.acme.notifications.hub.dispatcher.core.model.impl.content.DefaultSlackContent;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/slack")
public class SlackController {
    private final NotificationFacade facade;
    public SlackController(NotificationFacade facade) { this.facade = facade; }

    @PostMapping
    public ResponseEntity<SendResponse> send(@Valid @RequestBody SendSlackRequest req) {
        var notification = new DefaultSlackNotification(
                new DefaultRecipient(req.channelId(), "slack-channel"),
                new DefaultSlackContent(req.text(), req.blocks()),
                Priority.LOW,
                java.util.Map.of()
        );
        SendResult r = facade.send(notification);
        return ResponseEntity.ok(new SendResponse(r.success(), r.provider(), r.providerMessageId(), r.errorCode(), r.errorMessage()));
    }
}