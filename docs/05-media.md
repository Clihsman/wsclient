# Subida de media

Para reutilizar un mismo archivo en varios mensajes (en vez de enviar siempre un `link` público), súbelo primero a la WhatsApp Cloud API con `WhatsAppMediaService` y usa el `id` que retorna.

`WhatsAppMediaService` se configura de forma independiente al `WhatsAppClient` (comparte el mismo `WhatsAppService` internamente, pero necesita su propia llamada a `configureWhatsAppApi`):

```java
import com.wsclient.api.services.WhatsAppMediaService;
import com.wsclient.api.services.WhatsAppMediaServiceImpl;

WhatsAppMediaService mediaService = new WhatsAppMediaServiceImpl(service); // mismo WhatsAppService del cliente
mediaService.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0",
        "PHONE_NUMBER_ID",
        "ACCESS_TOKEN");
```

## Subir desde un `InputStream`

```java
import com.wsclient.api.messages.response.MediaResponse;
import java.io.FileInputStream;

try (FileInputStream in = new FileInputStream("catalog.pdf")) {
    MediaResponse response = mediaService.uploadMedia(in, "catalog.pdf", "application/pdf").join();
    String mediaId = response.id();
}
```

## Subir desde una ruta de archivo

```java
MediaResponse response = mediaService.uploadMedia("/path/to/catalog.pdf", "catalog.pdf", "application/pdf").join();
String mediaId = response.id();
```

## Usar el `id` subido para enviar el mensaje

```java
import com.wsclient.api.messages.request.Media;

Media document = Media.builder()
        .id(mediaId)
        .filename("catalog.pdf")
        .build();

client.sendDocumentAsync("573001112233", document).join();
```

El mismo patrón aplica a imagen, video, audio y sticker — usa `mediaService.uploadMedia(...)` para obtener el `id` y pásalo a `Media.builder().id(...)` en el `send*Async` correspondiente.
