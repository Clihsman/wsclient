# Envío de mensajes

Todos los métodos siguientes viven en `WhatsAppClient` y siguen el mismo patrón: validan el `to` (número en formato internacional, solo dígitos, sin `+`) y el payload; si algo falla, el `CompletableFuture` se completa con `IllegalArgumentException`. Ver [Manejo de errores](08-error-handling.md).

Para mensajes interactivos (botones/listas) ve a [Mensajes interactivos](03-interactive-messages.md); para plantillas, a [Plantillas](04-templates.md).

## Texto

```java
import com.wsclient.api.messages.request.Text;

Text text = Text.builder()
        .body("Hola, este es un mensaje de prueba.")
        .previewUrl(true) // opcional: muestra preview si el body contiene una URL
        .build();

client.sendTextAsync("573001112233", text).join();
```

Reglas: `body` requerido, entre 1 y 4096 caracteres.

## Imagen, video, audio y documento

Los cuatro comparten el mismo objeto `Media` (`com.wsclient.api.messages.request.Media`), con `id` **o** `link`, `caption` opcional y `filename` (solo para documentos):

```java
import com.wsclient.api.messages.request.Media;

// Por link
Media image = Media.builder()
        .link("https://example.com/photo.jpg")
        .caption("Foto de ejemplo")
        .build();
client.sendImageAsync("573001112233", image).join();

// Por id (subido previamente, ver "Subida de media")
Media video = Media.builder().id("1234567890").build();
client.sendVideoAsync("573001112233", video).join();

Media audio = Media.builder().link("https://example.com/audio.mp3").build();
client.sendAudioAsync("573001112233", audio).join();

Media document = Media.builder()
        .link("https://example.com/invoice.pdf")
        .filename("invoice.pdf") // requerido para documentos
        .build();
client.sendDocumentAsync("573001112233", document).join();
```

Reglas comunes: `id` o `link` requerido (si hay `link`, debe ser `http(s)://`); `caption` máximo 1024 caracteres. `filename` es **requerido** en documentos (máx. 240 caracteres) y **no permitido** en imagen/video/audio.

## Sticker

También usa `Media`, pero a diferencia de imagen/video/documento **no admite `caption` ni `filename`**:

```java
Media sticker = Media.builder()
        .link("https://example.com/sticker.webp")
        .build();

client.sendStickerAsync("573001112233", sticker).join();
```

## Ubicación

```java
import com.wsclient.api.messages.request.Location;

Location location = Location.builder()
        .latitude("4.710989")
        .longitude("-74.072092")
        .name("Bogotá")     // opcional
        .address("Colombia") // opcional, solo se muestra si hay name
        .build();

client.sendLocationAsync("573001112233", location).join();
```

Reglas: `latitude`/`longitude` requeridos, deben ser numéricos, `latitude` en `[-90, 90]` y `longitude` en `[-180, 180]`.

## Reacción

Reacciona a un mensaje previo usando su WhatsApp Message ID (WAMID):

```java
import com.wsclient.api.messages.request.Reaction;

Reaction reaction = Reaction.builder()
        .message_id("wamid.HBgL...")
        .emoji("👍")
        .build();

client.sendReactionAsync("573001112233", reaction).join();
```

Para **quitar** una reacción previamente enviada, envía el mismo `message_id` con `emoji("")` (string vacío, no `null`).

## Contactos

```java
import com.wsclient.api.messages.request.contact.Contact;
import com.wsclient.api.messages.request.contact.ContactName;
import com.wsclient.api.messages.request.contact.ContactPhone;
import java.util.List;

Contact contact = new Contact(
        null,                                                       // addresses
        null,                                                       // birthday
        null,                                                       // emails
        new ContactName("John Doe", "John", "Doe", null, null, null), // name (requerido)
        null,                                                       // org
        List.of(new ContactPhone("+573001112233", ContactPhone.ContactPhoneType.CELL, null)), // phones
        null);                                                      // urls

client.sendContactsAsync("573001112233", List.of(contact)).join();
```

Regla: la lista de contactos no puede estar vacía, y cada `Contact` debe tener un `name` con `formattedName` no vacío (es el único campo requerido dentro de `name`; el resto de `Contact` es opcional).
