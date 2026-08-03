# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

wsclient is a Java library that wraps the WhatsApp Cloud API (Meta Graph API), providing typed request/response models, input validation, and async client methods for sending messages, uploading media, managing business profiles, and handling webhooks. It is consumed as a dependency (built as a JAR), not run as a standalone application.

## Commands

```sh
mvn clean install          # Build and install to local repo (runs tests)
mvn test                   # Run all tests
mvn test -Dtest=WhatsAppServiceTest                       # Run a single test class
mvn test -Dtest=WhatsAppServiceTest#testConfigureWhatsAppApi_NullApiUrl   # Run a single test method
mvn package                 # Build target/wsclient.jar (and javadoc jar)
```

Java 21 is required (`maven.compiler.source/target` = 21). Tests live in `src/test/java/com/wsclient/` as a flat package (they do not mirror the `main` package structure).

## Architecture

### Two-layer client/service split

- **`api.services.WhatsAppClient` / `WhatsAppClientImpl`** — the public-facing API. Each `send*Async` method first runs input validation (via `WhatsAppInputValidator`), and if validation fails returns `CompletableFuture.failedFuture(exception)` instead of throwing synchronously. If validation passes, it builds a `Map<String, Object>` payload and delegates to `WhatsAppService.sendRequest`.
- **`api.services.WhatsAppService` / `WhatsAppServiceImpl`** — the low-level HTTP layer. Holds the configured `whatsappApiUrl`, `phoneNumberId`, `businessAccount`, and `token`. Exposes overloaded `sendRequest` methods for JSON payloads (`Map`/`ObjectNode`, built on Java's built-in `java.net.http.HttpClient`) and raw `HttpPost`/`HttpGet` (built on Apache `HttpClient` 4.x, used for multipart/media and templates). All error responses are converted into a checked `WhatsAppException` via `throwIfErrorResponse`.
- **`api.services.WhatsAppMediaService` / `WhatsAppMediaServiceImpl`** — separate service specifically for uploading media (multipart, via `httpmime`), independent from the JSON message-sending path.
- **`api.services.MetaWebhookService` / `MetaWebhookServiceImpl`** — handles subscribing an app to Meta webhook events (distinct from the `api.webhook` package, which models *incoming* payloads).

Both `WhatsAppClient.configureWhatsAppApi(...)` and `WhatsAppServiceImpl.configureWhatsAppApi(...)` call `ConfigValidator.validateConfig(...)`, so configuration is validated at both layers.

### Validators return exceptions, they don't throw them

`WhatsAppInputValidator` methods (e.g. `validateTextInput`, `validateInteractiveInput`, `validateMediaInput`, `validateImageInput`/`validateVideoInput`/`validateAudioInput`/`validateDocumentInput`) all follow the same convention: return the built `IllegalArgumentException` on failure, or `null` on success — they never throw directly. Callers (`WhatsAppClientImpl`) check for a non-null return and convert it into a failed future. This is different from `ConfigValidator`, which throws `IllegalArgumentException` directly. When adding new validation, follow the convention of the class you're extending.

Validation limits (button/list counts, text lengths, etc.) are centralized in `api.constants.WhatsAppConstants`.

### Message model packages

- `api.messages.request.*` — outbound message payload models (Text, Media, Template, Reaction, Location, Currency, DateTime, Component/Parameter for templates, etc.), plus `request.contact.*` and `request.interactive.*` for contact cards and interactive (button/list) messages.
- `api.messages.response.*` — models for parsing WhatsApp API responses (`WhatsAppResponse`, `WhatsAppErrorResponse`/`Error`/`ErrorData`, `MediaResponse`, `MessageData`, `ContactData`), plus `response.template.*` for template listing/retrieval responses.
- `api.webhook.*` — models mirroring Meta's inbound webhook payload structure (`WebhookMessageBody` → `Entry` → `Change` → `Value` → `Messages`/`Statuses`, etc.) for deserializing events received from Meta.
- `api.business.request` / `api.business.response` — WhatsApp Business Profile get/update models.

Note: both `api.messages.request` and `api.messages.response.template` define a `Component` class, and both `api.messages.request.interactive` and `api.webhook` define an `Interactive` class — pay attention to which one is imported when working across these packages.

Most request/response models use Lombok (`@Builder`, `@Getter`/`@Setter`, etc.) and are (de)serialized with Jackson `ObjectMapper`, frequently configured with `PropertyNamingStrategies.SNAKE_CASE` and `JsonInclude.Include.NON_NULL` to match the WhatsApp API's JSON conventions.

### InteractiveFactory

`api.messages.factory.InteractiveFactory` is a fluent builder for constructing `Interactive` button/list messages (`InteractiveFactory.createButton()...button(id, title)...build()` or `InteractiveFactory.createList()...section(title).row(id, title, desc)...build()`), used as an alternative to constructing `Interactive`/`InteractiveAction`/`InteractiveSection` objects by hand.

### Error handling

`core.exceptions.WhatsAppException` (checked) carries the WhatsApp API's error `message`, `type`, `code`, `errorSubcode`, and `fbtraceId`, parsed from `WhatsAppErrorResponse`. Since most public client methods return `CompletableFuture`, exceptions raised inside `supplyAsync` blocks (including `WhatsAppException`) are wrapped in `CompletionException`.
