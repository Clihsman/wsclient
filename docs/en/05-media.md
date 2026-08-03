# Media upload

To reuse the same file across several messages (instead of always sending a public `link`), upload it to the WhatsApp Cloud API first with `WhatsAppMediaService` and use the returned `id`.

`WhatsAppMediaService` is configured independently from `WhatsAppClient` (it shares the same `WhatsAppService` internally, but needs its own call to `configureWhatsAppApi`):

```java
import com.wsclient.api.services.WhatsAppMediaService;
import com.wsclient.api.services.WhatsAppMediaServiceImpl;

WhatsAppMediaService mediaService = new WhatsAppMediaServiceImpl(service); // same WhatsAppService as the client
mediaService.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0",
        "PHONE_NUMBER_ID",
        "ACCESS_TOKEN");
```

## Upload from an `InputStream`

```java
import com.wsclient.api.messages.response.MediaResponse;
import java.io.FileInputStream;

try (FileInputStream in = new FileInputStream("catalog.pdf")) {
    MediaResponse response = mediaService.uploadMedia(in, "catalog.pdf", "application/pdf").join();
    String mediaId = response.id();
}
```

## Upload from a file path

```java
MediaResponse response = mediaService.uploadMedia("/path/to/catalog.pdf", "catalog.pdf", "application/pdf").join();
String mediaId = response.id();
```

## Using the uploaded `id` to send a message

```java
import com.wsclient.api.messages.request.Media;

Media document = Media.builder()
        .id(mediaId)
        .filename("catalog.pdf")
        .build();

client.sendDocumentAsync("573001112233", document).join();
```

The same pattern applies to image, video, audio, and sticker — use `mediaService.uploadMedia(...)` to get the `id` and pass it to `Media.builder().id(...)` in the corresponding `send*Async`.
