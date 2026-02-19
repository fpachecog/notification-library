# notifications-hub

Librería **agnóstica a frameworks** para unificar el envío de **notificaciones** por **Email**, **SMS**, **Push (FCM)** y **Slack**, con **contenido tipado por canal**, arquitectura **Ports & Adapters (hexagonal)**, **reintentos**, envío **síncrono/asíncrono** y **configuración 100% en Java** (sin YAML/properties).

**Considerar:** El repositorio contiene 2 proyectos:
1) notifications-hub: Librería que unifica envío de notificaciones (Librería que se explica en detalle en el presente archivo README.md)
2) demo: Cliente que consume la librería

> **Estado de las integraciones**  
> Esta librería incluye **adaptadores simulados** (no hacen HTTP real). Su foco es la **arquitectura** y el **contrato**. En producción, reemplaza los simulados por clientes reales contra los proveedores.

---

## Índice

- [Instalación](#instalación)
    - [Maven](#maven)
    - [Gradle](#gradle)
- [Configuración](#configuración)
    - [Email (SendGrid / Mailgun)](#email-sendgrid--mailgun)
    - [SMS (Twilio)](#sms-twilio)
    - [Push (FCM)](#push-fcm)
    - [Slack](#slack)
    - [Reintentos y envío asíncrono](#reintentos-y-envío-asíncrono)
- [Agregar un proveedor](#agregar-un-proveedor)
- [Proveedores soportados](#proveedores-soportados)
- [API Reference](#api-reference)
    - [Dominio y tipos por canal](#dominio-y-tipos-por-canal)
    - [Fachada](#fachada)
    - [Puertos (Ports) hacia proveedores](#puertos-ports-hacia-proveedores)
    - [Configuración y estrategia](#configuración-y-estrategia)
- [Seguridad](#seguridad)
- [Cliente Demo](#demo)

---

## Instalación

### Maven

1) Agrega la dependencia a tu `pom.xml`:


```xml
<dependency>
  <groupId>com.acme.notifications</groupId>
  <artifactId>notifications-hub</artifactId>
  <version>1.0.0</version>
</dependency>
```

Si estás desarrollando localmente, primero instala la librería en tu ~/.m2:

mvn -q -DskipTests clean install

### Gradle
```groovie
dependencies {
    implementation 'com.acme.notifications:notifications-hub:1.0.0'
}
```

### Configuración
La configuración es 100% Java mediante NotificationConfigBuilder:

* Definir remitentes por defecto: defaultFromEmail(...) y/o defaultFromSms(...).
* Registrar proveedores por canal: registerEmail/Sms/Push/Slack(alias, client).
* Estrategia de selección y fallback: selection(...).
* Reintentos: retry(...).
* Asíncrono: async(...).




### Email (SendGrid / Mailgun)

Configuración para SendGrid

```java

NotificationConfig cfg = new NotificationConfigBuilder()
  .defaultFromEmail("noreply@acme.com")
  .registerEmail("sendgrid", new SendGridEmailClient(System.getenv("SENDGRID_API_KEY")))
  .selection(new PrimaryFallbackProviderSelection(
      Map.of(ChannelType.EMAIL, List.of("sendgrid","mailgun"))
  ))
  .build();
```

Configuración para Mailgun

```java

NotificationConfig cfg = new NotificationConfigBuilder()
  .defaultFromEmail("noreply@acme.com")
  .registerEmail("mailgun",  new MailgunEmailClient(System.getenv("MAILGUN_API_KEY"), "mg.acme.com"))
  .selection(new PrimaryFallbackProviderSelection(
      Map.of(ChannelType.EMAIL, List.of("sendgrid","mailgun"))
  ))
  .build();
```

Enviar Email:

```java
// Inyectar: NotificationFacade facade


// usar el método facade.send
EmailNotification email = new DefaultEmailNotification(
  new DefaultRecipient("user@example.com", "User"),
  new DefaultEmailContent("Subject", "Text body", "<b>HTML body</b>", List.of(/*attachments*/)),
  Priority.NORMAL,
  Map.of("name", "User")
);
facade.send(email);
```
### SMS (Twilio)

Configuración para Twilio

```java
NotificationConfig cfg = new NotificationConfigBuilder()
  .defaultFromSms("+12345678900")
  .registerSms("twilio", new TwilioSmsClient(
      System.getenv("TWILIO_ACCOUNT_SID"),
      System.getenv("TWILIO_AUTH_TOKEN")
  ))
  .selection(new PrimaryFallbackProviderSelection(
      Map.of(ChannelType.SMS, List.of("twilio"))
  ))
  .build();
```

Enviar SMS

```java
// Inyectar: NotificationFacade facade


// usar el método facade.send
SmsNotification sms = new DefaultSmsNotification(
  new DefaultRecipient("+56911111111", "Ada"),
  new DefaultSmsContent("Your code is 123456",
      com.acme.notifications.hub.dispatcher.core.model.api.content.SmsEncoding.GSM_7BIT, 160),
  Priority.HIGH,
  Map.of()
);
facade.send(sms);

```
### Push (FCM)

Cnfiguración para FCM
```java
NotificationConfig cfg = new NotificationConfigBuilder()
  .registerPush("fcm", new FcmPushClient(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
  .selection(new PrimaryFallbackProviderSelection(
      Map.of(ChannelType.PUSH, List.of("fcm"))
  ))
  .build();
```

Enviar Push:
```java
// Inyectar: NotificationFacade facade


// usar el método facade.send
PushNotification push = new DefaultPushNotification(
        new DefaultRecipient("device_token_abc", "Alice's device"),
        new DefaultPushContent("Hi!", "You have a message", Map.of("screen","inbox")),
        Priority.NORMAL,
        Map.of()
);
facade.send(push);
```

### Slack
```java
NotificationConfig cfg = new NotificationConfigBuilder()
        .registerSlack("slack", new SlackApiClient(System.getenv("SLACK_BOT_TOKEN")))
        .selection(new PrimaryFallbackProviderSelection(
                Map.of(ChannelType.SLACK, List.of("slack"))
        ))
        .build();
```

Enviar Slack:

```java

// Inyectar: NotificationFacade facade


// usar el método facade.send
SlackNotification slack = new DefaultSlackNotification(
  new DefaultRecipient("C024BE7LH", "alerts-channel"),
  new DefaultSlackContent("Build succeeded :tada:", List.of(/*blocks opcionales*/)),
  Priority.LOW,
  Map.of()
);
facade.send(slack);
```

### Reintentos y envío asíncrono

```java
NotificationConfig cfg = new NotificationConfigBuilder()
  // ...
  .retry(NotificationConfigBuilder.fixedRetry(3, 200)) // 3 intentos, 200 ms de backoff
  .async(NotificationConfigBuilder.async(Executors.newFixedThreadPool(4))) // habilita sendAsync
  .build();

// Uso
facade.sendAsync(email).thenAccept(result -> { /* ... */ }).join();
```

### Agregar un proveedor
Ejemplo: agregar un nuevo proveedor de Email (“AcmeMail”).

1) Implementar el port EmailProviderClient:

```java
import com.acme.notifications.hub.dispatcher.application.port.EmailProviderClient;
import com.acme.notifications.hub.dispatcher.core.model.api.content.EmailAttachment;
import com.acme.notifications.hub.dispatcher.core.service.SendResult;
import com.acme.notifications.hub.dispatcher.infrastructure.client.SimulatedSendResult;
import java.util.List;

public final class AcmeMailEmailClient implements EmailProviderClient {
  private final String apiKey;
  public AcmeMailEmailClient(String apiKey) { this.apiKey = apiKey; }

  @Override
  public SendResult sendEmail(String from, String to, String subject, String textBody, String htmlBody) {
    // Llamada a API de Email
    return new SimulatedSendResult(true, "acme-mail", "acme-msg-1", null, null);
  }

  @Override
  public SendResult sendEmailWithAttachments(String from, String to, String subject, String textBody, String htmlBody,
                                             List<EmailAttachment> attachments) {
    return new SimulatedSendResult(true, "acme-mail", "acme-msg-1-attach", null, null);
  }
}
```
2) Registrar proveedor y definir la prioridad/fallback:
```java
NotificationConfig cfg = new NotificationConfigBuilder()
  .defaultFromEmail("noreply@acme.com")
  .registerEmail("acme", new AcmeMailEmailClient(System.getenv("ACME_MAIL_API_KEY")))
  .selection(new PrimaryFallbackProviderSelection(
      Map.of(ChannelType.EMAIL, List.of("acme","mailgun")) // “acme” primario, “mailgun” fallback
  ))
  .build();
```

### Proveedores soportados
Simulados e incluidos:

* Email: SendGridEmailClient, MailgunEmailClient
* SMS: TwilioSmsClient
* Push: FcmPushClient
* Slack: SlackApiClient




### API Reference

### Dominio y tipos por canal

* ChannelType { EMAIL, SMS, PUSH, SLACK }
* Priority { LOW, NORMAL, HIGH }
* Recipient { id(), displayName() } → id = email/phone/token/channelId según canal
* Notificaciones (1 por canal):
  * EmailNotification extends Notification → EmailContent(subject, textBody, htmlBody, List<EmailAttachment>)
  * SmsNotification extends Notification   → SmsContent(body, encoding, maxLength, length)
  * PushNotification extends Notification  → PushContent(title, body, Map<String,String> data)
  * SlackNotification extends Notification → SlackContent(text, List<Map<String,Object>> blocks)



Implementaciones inmutables (records):

* DefaultEmailNotification, DefaultSmsNotification, DefaultPushNotification, DefaultSlackNotification
* DefaultEmailContent, DefaultSmsContent, DefaultPushContent, DefaultSlackContent
* DefaultRecipient, DefaultEmailAttachment

### Fachada

* NotificationFacade
  * SendResult send(Notification n)
  * List<SendResult> sendBatch(List<Notification> list)
  * CompletableFuture<SendResult> sendAsync(Notification n)

* SendResult
  * boolean success()
  * String provider()
  * String providerMessageId()
  * String errorCode()
  * String errorMessage()



### Puertos (Ports) hacia proveedores

* EmailProviderClient
  * SendResult sendEmail(String from, String to, String subject, String textBody, String htmlBody)
  * SendResult sendEmailWithAttachments(String from, String to, String subject, String textBody, String htmlBody, List<EmailAttachment> attachments)

* SmsProviderClient
  * SendResult sendSms(String from, String to, String body)

* PushProviderClient
  * SendResult sendPush(String deviceToken, String title, String body, Map<String,String> data)

* SlackProviderClient
  * SendResult postMessage(String channel, String text, List<Map<String,Object>> blocks)


### Configuración y estrategia


* NotificationConfig:
  * defaultFromEmail(), defaultFromSms()
  * emailProviders(), smsProviders(), pushProviders(), slackProviders()
  * providerSelectionStrategy(), retryPolicy(), asyncConfig()



### Seguridad

A diferencia de un framework como Spring Security, esta librería no incluye componentes de seguridad “externos” (como OAuth2, JWT, firma digital, etc.), sino que aplica mecanismos de seguridad internos, centrados en garantizar consistencia, validación, aislamiento, prevención de fugas de datos sensibles y resiliencia ante fallas.
* Validar inputs.
* Evitar fugas de información sensible.
* Mantener aislamiento entre canales/proveedores.
* Proteger la infraestructura de errores previsibles.
* Evitar coupling que permita misuse.
* Prevenir malas prácticas comunes en integraciones externas.

### Demo
Se trata del proyecto "demo" presente en el repositorio. Es un proyecto en spring boot 3.5 y java 21, que exponer 4 endpoints

