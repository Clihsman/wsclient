# Manejo de errores

La librería usa tres mecanismos de error distintos, dependiendo de la capa:

## 1. `IllegalArgumentException` — validación de input

Antes de llamar a la API, los métodos de envío validan sus parámetros (número de destino, campos requeridos, límites de longitud, etc. — ver [Límites y validación](09-limits-and-validation.md)). Si la validación falla:

- En los métodos `send*Async` de `WhatsAppClient`, el `CompletableFuture` se completa con la excepción (no se lanza de forma síncrona), así que siempre te llega envuelta en `CompletionException` al hacer `.join()`/`.get()`:

  ```java
  try {
      client.sendTextAsync(to, text).join();
  } catch (CompletionException e) {
      if (e.getCause() instanceof IllegalArgumentException iae) {
          System.err.println("Input inválido: " + iae.getMessage());
      }
  }
  ```

- Unas pocas operaciones (`configureWhatsAppApi`, `updateBusinessProfile`) lanzan `IllegalArgumentException` **directamente y de forma síncrona**, sin pasar por el `CompletableFuture` — captúralas con un `try/catch` normal alrededor de la llamada, no dentro del `.exceptionally(...)`.

## 2. `WhatsAppException` — error reportado por la API de Meta

Cuando la API de WhatsApp responde con un status distinto de `200`, la librería parsea el cuerpo de error y lanza `com.wsclient.core.exceptions.WhatsAppException` (excepción *checked*), que llega envuelta en `CompletionException` dentro de los futures. Expone los mismos campos que retorna Meta:

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

## 3. `IOException` / errores de red

Si la petición HTTP falla por un problema de red (timeout, DNS, conexión rechazada, etc.), la excepción también llega como `CompletionException` con un `IOException` como causa.

## Resumen por método

| Situación | Cómo se manifiesta |
|---|---|
| Input inválido en `send*Async` (`WhatsAppClient`) | `CompletionException` → causa `IllegalArgumentException` |
| Input inválido en `configureWhatsAppApi` / `updateBusinessProfile` | `IllegalArgumentException` lanzada directamente (síncrona) |
| Meta responde con error HTTP | `CompletionException` → causa `WhatsAppException` |
| Falla de red / IO | `CompletionException` → causa `IOException` |

En todos los casos donde el error llega envuelto, `Throwable.getCause()` sobre la `CompletionException` te da la excepción real para hacer `instanceof`/manejo específico.
