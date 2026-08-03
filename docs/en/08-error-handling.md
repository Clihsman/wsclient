# Error handling

The library uses three distinct error mechanisms, depending on the layer:

## 1. `IllegalArgumentException` — input validation

Before calling the API, sending methods validate their parameters (recipient number, required fields, length limits, etc. — see [Limits and validation](09-limits-and-validation.md)). If validation fails:

- In `WhatsAppClient`'s `send*Async` methods, the `CompletableFuture` completes with the exception (it is not thrown synchronously), so it always arrives wrapped in `CompletionException` when you call `.join()`/`.get()`:

  ```java
  try {
      client.sendTextAsync(to, text).join();
  } catch (CompletionException e) {
      if (e.getCause() instanceof IllegalArgumentException iae) {
          System.err.println("Invalid input: " + iae.getMessage());
      }
  }
  ```

- A few operations (`configureWhatsAppApi`, `updateBusinessProfile`) throw `IllegalArgumentException` **directly and synchronously**, without going through the `CompletableFuture` — catch them with a plain `try/catch` around the call, not inside `.exceptionally(...)`.

## 2. `WhatsAppException` — error reported by the Meta API

When the WhatsApp API responds with a status other than `200`, the library parses the error body and throws `com.wsclient.core.exceptions.WhatsAppException` (a checked exception), which arrives wrapped in `CompletionException` inside futures. It exposes the same fields Meta returns:

```java
client.sendTextAsync(to, text)
        .exceptionally(ex -> {
            if (ex.getCause() instanceof WhatsAppException wae) {
                System.err.println("type=" + wae.getType()
                        + " code=" + wae.getCode()
                        + " subcode=" + wae.getErrorSubcode()
                        + " fbtrace_id=" + wae.getFbtraceId()
                        + " message=" + wae.getMessage());
            }
            return null;
        });
```

## 3. `IOException` / network errors

If the HTTP request fails due to a network problem (timeout, DNS, connection refused, etc.), the exception also arrives as `CompletionException` with an `IOException` as its cause.

## Summary by method

| Situation | How it manifests |
|---|---|
| Invalid input in `send*Async` (`WhatsAppClient`) | `CompletionException` → cause is `IllegalArgumentException` |
| Invalid input in `configureWhatsAppApi` / `updateBusinessProfile` | `IllegalArgumentException` thrown directly (synchronous) |
| Meta responds with an HTTP error | `CompletionException` → cause is `WhatsAppException` |
| Network / IO failure | `CompletionException` → cause is `IOException` |

In every case where the error arrives wrapped, `Throwable.getCause()` on the `CompletionException` gives you the real exception for `instanceof`/specific handling.
