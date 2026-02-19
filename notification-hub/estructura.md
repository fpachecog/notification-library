Estructura del módulo (packages por capas)
Todo en un único módulo y un único pom, con la separación por packages:
src
└─ main
└─ java
└─ com.acme.notifications.hub.dispatcher
├─ core
│   ├─ model
│   │   ├─ api
│   │   │   ├─ content   (EmailContent, SmsContent, etc.)
│   │   │   └─ ...       (ChannelType, Recipient, ...)
│   │   └─ impl          (records inmutables)
│   ├─ facade            (NotificationFacade interface)
│   ├─ service           (SendResult, Validator interfaces)
│   ├─ event             (si defines eventos)
│   └─ error             (DomainException, etc.)
├─ application
│   ├─ port              (EmailProviderClient, SmsProviderClient, ...)
│   ├─ strategy          (ProviderSelectionStrategy...)
│   ├─ service
│   │   ├─ channels      (ChannelSender + Email/SMS/Push/Slack impl)
│   │   └─ impl          (NotificationFacadeImpl)
│   ├─ validator         (PerChannelNotificationValidator)
│   ├─ template          (opcional; simple engine)
│   └─ config            (NotificationConfig + Builder)
└─ infrastructure
├─ client
│   ├─ email         (SendGridEmailClient, MailgunEmailClient)
│   ├─ sms           (TwilioSmsClient)
│   ├─ push          (FcmPushClient)
│   └─ slack         (SlackApiClient)
└─ observability     (SimulatedSendResult, logging)


Reglas enforceables aun en un módulo:

core no debe depender de application/infrastructure.
application puede depender de core, no de clases concretas de infrastructure (sólo ports).
infrastructure puede depender de application y core.
Lo validamos con ArchUnit en src/test/java (te dejo un snippet más abajo).



Detalle de lo implementado (end-to-end)
1) Dominio (core)

ChannelType: EMAIL, SMS, PUSH, SLACK.
Recipient: común a todos los canales (id = email/phone/deviceToken/channelId según canal).
Priority: LOW/NORMAL/HIGH.
Notification (base): canal, Recipient, Priority, metadata.
Contenido tipado por canal (interfaces)

EmailContent: subject, textBody, htmlBody, attachments (cada adjunto tiene filename, contentType, data[]). Esto mapea naturalmente a SendGrid v3 (subject, content[text/html] en POST /v3/mail/send) y Mailgun (subject, text, html en POST /v3/{domain}/messages). [twilio.com], [documentat...ailgun.com]
SmsContent: body, encoding (GSM_7BIT/UCS2), maxLength y length() calculado. Twilio exige To, From, Body (y manejarás segmentación/encoding aguas arriba). [help.twilio.com]
PushContent: title, body, data (pares clave-valor), alineado con FCM HTTP v1 (message.token, notification{title,body}, data). [firebase.google.com]
SlackContent: text, blocks (lista de mapas), como chat.postMessage (channel + text/blocks). [docs.slack.dev]


Notificaciones por canal: EmailNotification, SmsNotification, PushNotification, SlackNotification (todas extienden Notification pero exponen su content específico).
Facade (interface): NotificationFacade con send, sendBatch, sendAsync.
Contratos de dominio: SendResult (estado del envío y metadatos) y NotificationValidator (reglas de validación por canal).


Ventaja: El dominio queda claro y fuertemente tipado por canal, cumpliendo tu pedido de evitar el contenido genérico.


2) Capa de aplicación
   Puertos (ports) → proveedores reales (simulados aquí)

EmailProviderClient: sendEmail(...) y sendEmailWithAttachments(...) (adjuntos si el proveedor los soporta; SendGrid/Mailgun los soportan). [twilio.com], [documentat...ailgun.com]
SmsProviderClient: sendSms(from, to, body) (mínimos de Twilio Messages: To, From, Body). [help.twilio.com]
PushProviderClient: sendPush(token, title, body, data) (FCM v1). [firebase.google.com]
SlackProviderClient: postMessage(channel, text, blocks) (chat.postMessage). [docs.slack.dev]


DIP total: la app orquesta contra interfaces; los adaptadores reales (infra) implementan estos puertos.

Estrategia por canal (ChannelSender) y selección de proveedor

ChannelSender: interfaz “Strategy por canal” con channel() y send(Notification).
Implementaciones:

EmailChannelSender (orquesta EmailProviderClient y adjuntos)
SmsChannelSender
PushChannelSender
SlackChannelSender


ProviderSelectionStrategy: PrimaryFallbackProviderSelection permite definir prioridad/fallback por canal.
(Si SendGrid falla, usa Mailgun; si Twilio falla y hay otro SMS provider, se aplica el orden.)


OCP: para agregar un canal no modificas la facade, sólo registras un ChannelSender más y su port.

Configuración sólo con Java (Builder)

NotificationConfig: contiene defaultFromEmail, defaultFromSms, mapas de providers por canal, la ProviderSelectionStrategy, RetryPolicy (máx intentos/backoff) y AsyncConfig (ejecutor para CompletableFuture).
NotificationConfigBuilder: Fluent API para registrar proveedores (registerEmail/sms/push/slack), setear defaultFrom*, retry(...), async(...), selection(...).
(No hay YAML, ni properties, ni anotaciones de framework.)

Validación y reintentos

PerChannelNotificationValidator:

Email: email válido + subject no vacío (coherente con proveedores como SendGrid/Mailgun). [twilio.com], [documentat...ailgun.com]
SMS: E.164 básico y chequeos de longitud/encoding (Twilio trata E.164 y segmenta, pero validamos el caso simple). [help.twilio.com]
Push: exige device token (FCM HTTP v1 requiere message.token). [firebase.google.com]
Slack: validador básico de formato channel/user id (ej. CXXXX...), acorde a chat.postMessage que espera un channel válido. [docs.slack.dev]


Reintentos: la facade envuelve el ChannelSender.send(...) con doWithRetry según RetryPolicy (intentos + backoff).
Asíncrono: sendAsync usa CompletableFuture con el Executor de AsyncConfig.

Facade (implementación)

NotificationFacadeImpl:

Registra un ChannelSender por canal (email/sms/push/slack).
send(...) → valida → elige el ChannelSender del Notification.channel() → reintenta según política → retorna SendResult.
sendBatch(...) → itera.
sendAsync(...) → CompletableFuture si hay AsyncConfig.enabled().




3) Capa de infraestructura (adaptadores simulados)

No hay HTTP real; simulamos los envíos y retornamos SimulatedSendResult.
El shape de cada cliente refleja los campos y endpoints reales de los proveedores:


SendGridEmailClient: simula POST /v3/mail/send con from, personalizations.to, subject, content[] (text/html). Soporta adjuntos lógicamente. [twilio.com]
MailgunEmailClient: simula POST /v3/{domain}/messages (multipart/form-data) con from, to, subject, text/html y adjuntos. [documentat...ailgun.com]
TwilioSmsClient: simula Messages REST con To, From, Body mínimos. [help.twilio.com]
FcmPushClient: simula HTTP v1 con message.token, notification{title,body}, data. [firebase.google.com]
SlackApiClient: simula chat.postMessage (channel, text o blocks)