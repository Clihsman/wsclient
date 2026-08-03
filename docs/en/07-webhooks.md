# Webhooks

wsclient covers two parts of the webhook lifecycle: **subscribing** your app to WhatsApp events, and **receiving/parsing/verifying** the events Meta sends to your endpoint.

## Subscribing the app to events

```java
import com.wsclient.api.services.MetaWebhookService;
import com.wsclient.api.services.MetaWebhookServiceImpl;

MetaWebhookService webhookService = new MetaWebhookServiceImpl();
webhookService.configure("https://graph.facebook.com/v20.0");

webhookService.subscribeApp(
        "META_APP_ID",
        "ACCESS_TOKEN",
        "https://your-domain.com/webhooks/whatsapp", // callback_url
        "VERIFY_TOKEN"
).join();
```

If Meta responds with an error, the future fails with `CompletionException` wrapping a `WhatsAppException` (or `IOException` if there was a network issue).

## Endpoint verification (`GET` handshake)

When registering the webhook in the Meta dashboard, your endpoint will receive a `GET` with `hub.mode`, `hub.verify_token`, and `hub.challenge`. wsclient does **not** provide an HTTP handler for this because it depends on your framework — the logic is simply: if `hub.verify_token` matches the token you configured in Meta, respond `200` with the value of `hub.challenge`.

## Verifying an incoming event's signature (`POST`)

Every `POST` Meta sends includes an `X-Hub-Signature-256` header signed with your App Secret. **Always verify it before processing the payload**, to confirm the request really comes from Meta:

```java
import com.wsclient.common.utils.WhatsAppUtils;

// rawBody: the raw request body, exactly as received (before deserializing)
// signatureHeader: the value of the "X-Hub-Signature-256" header
boolean isValid = WhatsAppUtils.verifyWebhookSignature(rawBody, signatureHeader, appSecret);

if (!isValid) {
    // respond 401/403 and do not process the payload
}
```

- A `null` `payload` or `appSecret` throws `IllegalArgumentException` (a programming error: it means you didn't configure the App Secret).
- A missing, malformed, or non-matching `signatureHeader` returns `false` (does not throw) — this is the expected outcome for a request that isn't actually from Meta.

## Parsing the payload

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsclient.api.webhook.WebhookMessageBody;
import com.wsclient.api.webhook.Messages;

ObjectMapper mapper = new ObjectMapper();
WebhookMessageBody body = mapper.readValue(rawBody, WebhookMessageBody.class);

for (var entry : body.entry()) {
    for (var change : entry.changes()) {
        var value = change.value();

        if (value.messages() != null) {
            for (Messages message : value.messages()) {
                switch (message.type()) {
                    case TEXT -> System.out.println(message.from() + ": " + message.text().body());
                    case IMAGE -> System.out.println("Image received: " + message.image().id());
                    case INTERACTIVE -> System.out.println("Interactive reply: " + message.interactive());
                    default -> System.out.println("Unhandled type: " + message.type());
                }
            }
        }

        if (value.statuses() != null) {
            value.statuses().forEach(status -> System.out.println("Status: " + status));
        }
    }
}
```

The `com.wsclient.api.webhook.*` models cover incoming text, image, video, audio, document, sticker, location, contacts, interactive, button, system messages, and delivery/read statuses.

## Extracting a message's internal ID

WAMIDs (`wamid.<base64>`) received in webhooks can be decoded with:

```java
String decoded = WhatsAppUtils.extractMessageId(message.id());
```

Throws `IllegalArgumentException` if the format isn't `wamid.<base64>` or the base64 is invalid.
