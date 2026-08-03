# wsclient documentation

**Language:** English | [Español](../es/README.md)

wsclient is a Java library for interacting with the WhatsApp Cloud API (Meta Graph API): sending messages, uploading media, managing the business profile, and handling webhooks.

This folder documents how to **use** the library. To understand its internal architecture (packages, layers, code conventions), see [`CLAUDE.md`](../../CLAUDE.md) at the root of the repository.

## Index

1. [Getting started](01-getting-started.md) — dependency, client configuration, async pattern.
2. [Sending messages](02-sending-messages.md) — text, image, video, audio, document, sticker, location, reaction, contacts.
3. [Interactive messages](03-interactive-messages.md) — buttons, lists, single/multi-product, and CTA URL buttons with `InteractiveFactory`.
4. [Templates](04-templates.md) — sending, listing, creating, editing, and deleting templates.
5. [Media upload](05-media.md) — upload, retrieve, and delete files, and reuse their `id` in messages.
6. [Business profile](06-business-profile.md) — read and update the Business Profile.
7. [Webhooks](07-webhooks.md) — subscribe the app, verify Meta's signature, parse incoming events.
8. [Error handling](08-error-handling.md) — `WhatsAppException`, `CompletionException` and validation.
9. [Limits and validation](09-limits-and-validation.md) — constants and rules the library applies before calling the API.
10. [Business account management](10-business-management.md) — phone numbers and QR codes.
11. [OAuth](11-oauth.md) — App Access Token, code exchange, and long-lived tokens.

## Minimal example

```java
WhatsAppService service = new WhatsAppServiceImpl();
WhatsAppClient client = new WhatsAppClientImpl(service);

client.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0",
        "PHONE_NUMBER_ID",
        "ACCESS_TOKEN");

Text text = Text.builder().body("Hello from wsclient!").build();

client.sendTextAsync("573001112233", text)
        .thenAccept(response -> System.out.println(response.messages()))
        .exceptionally(ex -> {
            System.err.println("Error sending message: " + ex.getCause().getMessage());
            return null;
        });
```
