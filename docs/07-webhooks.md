# Webhooks

wsclient cubre dos partes del ciclo de vida de un webhook: **suscribir** tu app a los eventos de WhatsApp, y **recibir/parsear/verificar** los eventos que Meta envía a tu endpoint.

## Suscribir la app a los eventos

```java
import com.wsclient.api.services.MetaWebhookService;
import com.wsclient.api.services.MetaWebhookServiceImpl;

MetaWebhookService webhookService = new MetaWebhookServiceImpl();
webhookService.configure("https://graph.facebook.com/v20.0");

webhookService.subscribeApp(
        "META_APP_ID",
        "ACCESS_TOKEN",
        "https://tu-dominio.com/webhooks/whatsapp", // callback_url
        "VERIFY_TOKEN"
).join();
```

Si Meta responde con un error, el future falla con `CompletionException` envolviendo un `WhatsAppException` (o `IOException` si hubo un problema de red).

## Verificación del endpoint (handshake `GET`)

Al registrar el webhook en el panel de Meta, tu endpoint recibirá un `GET` con `hub.mode`, `hub.verify_token` y `hub.challenge`. wsclient **no** provee un handler HTTP para esto porque depende de tu framework — la lógica es simplemente: si `hub.verify_token` coincide con el token que configuraste en Meta, responde `200` con el valor de `hub.challenge`.

## Verificar la firma de un evento entrante (`POST`)

Cada `POST` que Meta envía incluye un header `X-Hub-Signature-256` firmado con tu App Secret. **Siempre verifícalo antes de procesar el payload**, para confirmar que la petición realmente viene de Meta:

```java
import com.wsclient.common.utils.WhatsAppUtils;

// rawBody: el cuerpo crudo del request, exactamente como llegó (antes de deserializar)
// signatureHeader: el valor del header "X-Hub-Signature-256"
boolean isValid = WhatsAppUtils.verifyWebhookSignature(rawBody, signatureHeader, appSecret);

if (!isValid) {
    // responde 401/403 y no proceses el payload
}
```

- `payload` y `appSecret` nulos lanzan `IllegalArgumentException` (error de programación: significa que no configuraste el App Secret).
- Un `signatureHeader` ausente, mal formado o que no coincide con el HMAC calculado retorna `false` (no lanza excepción) — es el resultado esperado para un request que no viene de Meta.

## Parsear el payload

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
                    case IMAGE -> System.out.println("Imagen recibida: " + message.image().id());
                    case INTERACTIVE -> System.out.println("Respuesta interactiva: " + message.interactive());
                    default -> System.out.println("Tipo no manejado: " + message.type());
                }
            }
        }

        if (value.statuses() != null) {
            value.statuses().forEach(status -> System.out.println("Status: " + status));
        }
    }
}
```

Los modelos de `com.wsclient.api.webhook.*` cubren mensajes entrantes de texto, imagen, video, audio, documento, sticker, ubicación, contactos, interactivos, botones, sistema y statuses de entrega/lectura.

## Extraer el ID interno de un mensaje

Los WAMIDs (`wamid.<base64>`) que llegan en los webhooks pueden decodificarse con:

```java
String decoded = WhatsAppUtils.extractMessageId(message.id());
```

Lanza `IllegalArgumentException` si el formato no es `wamid.<base64>` o si el base64 no es válido.
