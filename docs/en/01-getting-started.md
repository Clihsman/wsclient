# Getting started

## Requirements

- Java 21 or later.
- Maven.
- A WhatsApp Business number configured in Meta (`phoneNumberId`) and an access token (`ACCESS_TOKEN`) with permissions on the WhatsApp Cloud API.

## Adding the dependency

wsclient is not published on Maven Central. Clone the repository and install it into your local Maven repository:

```sh
git clone https://github.com/Clihsman/wsclient.git
cd wsclient
mvn clean install
```

Then, in your project's `pom.xml`:

```xml
<dependency>
    <groupId>com.wsclient</groupId>
    <artifactId>wsclient</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Initializing the client

The main entry point is `WhatsAppClient`, which wraps `WhatsAppService` (the layer that speaks HTTP to the Meta API):

```java
import com.wsclient.api.services.WhatsAppClient;
import com.wsclient.api.services.WhatsAppClientImpl;
import com.wsclient.api.services.WhatsAppService;
import com.wsclient.api.services.WhatsAppServiceImpl;

WhatsAppService service = new WhatsAppServiceImpl();
WhatsAppClient client = new WhatsAppClientImpl(service);

client.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0", // whatsappApiUrl
        "PHONE_NUMBER_ID",                  // phoneNumberId
        "ACCESS_TOKEN");                    // token
```

`configureWhatsAppApi` validates the three parameters before continuing and throws `IllegalArgumentException` if:

- `whatsappApiUrl` is null/empty or doesn't start with `http`.
- `phoneNumberId` is null/empty or contains non-digit characters.
- `token` is null/empty.

> **Note — WhatsApp Business Account ID (WABA):** `WhatsAppClient.configureWhatsAppApi` does not take the `businessAccount` (WABA ID). That value is only needed to list templates (see [Templates](04-templates.md)); if you need it, configure `WhatsAppService` directly:
> ```java
> service.configureWhatsAppApi(apiUrl, phoneNumberId, businessAccountId, token);
> ```

## The async pattern

Every method that calls the API returns `CompletableFuture<WhatsAppResponse>` (or the corresponding response type). Internally:

1. Input is validated synchronously. If validation fails, the `CompletableFuture` completes immediately with the exception (`CompletableFuture.failedFuture(...)`) — no HTTP call is made.
2. If validation passes, the HTTP call happens inside `CompletableFuture.supplyAsync(...)`, and any exception (network failure, or `WhatsAppException` if the Meta API responds with an error) is wrapped in `java.util.concurrent.CompletionException`.

This means you should **always** handle the result as a future, either blocking (`.join()`/`.get()`, typical in scripts or tests) or reactively (`.thenAccept(...)`/`.exceptionally(...)`, recommended in applications):

```java
client.sendTextAsync(to, text)
        .thenAccept(response -> log.info("Sent: {}", response))
        .exceptionally(ex -> {
            Throwable cause = ex.getCause(); // IllegalArgumentException or WhatsAppException
            log.error("Failed to send message", cause);
            return null;
        });
```

See [Error handling](08-error-handling.md) for the details of which exception to expect in each case.
