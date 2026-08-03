# Sending messages

All the methods below live in `WhatsAppClient` and follow the same pattern: they validate `to` (international format, digits only, no `+`) and the payload; if anything fails, the `CompletableFuture` completes with `IllegalArgumentException`. See [Error handling](08-error-handling.md).

For interactive messages (buttons/lists) see [Interactive messages](03-interactive-messages.md); for templates, see [Templates](04-templates.md).

## Text

```java
import com.wsclient.api.messages.request.Text;

Text text = Text.builder()
        .body("Hello, this is a test message.")
        .previewUrl(true) // optional: shows a preview if the body contains a URL
        .build();

client.sendTextAsync("573001112233", text).join();
```

Rules: `body` required, between 1 and 4096 characters.

## Image, video, audio and document

All four share the same `Media` object (`com.wsclient.api.messages.request.Media`), with `id` **or** `link`, optional `caption`, and `filename` (documents only):

```java
import com.wsclient.api.messages.request.Media;

// By link
Media image = Media.builder()
        .link("https://example.com/photo.jpg")
        .caption("Example caption")
        .build();
client.sendImageAsync("573001112233", image).join();

// By id (previously uploaded, see "Media upload")
Media video = Media.builder().id("1234567890").build();
client.sendVideoAsync("573001112233", video).join();

Media audio = Media.builder().link("https://example.com/audio.mp3").build();
client.sendAudioAsync("573001112233", audio).join();

Media document = Media.builder()
        .link("https://example.com/invoice.pdf")
        .filename("invoice.pdf") // required for documents
        .build();
client.sendDocumentAsync("573001112233", document).join();
```

Common rules: `id` or `link` required (if `link` is present, it must be `http(s)://`); `caption` max 1024 characters. `filename` is **required** for documents (max 240 characters) and **not allowed** for image/video/audio.

## Sticker

Also uses `Media`, but unlike image/video/document it **does not support `caption` or `filename`**:

```java
Media sticker = Media.builder()
        .link("https://example.com/sticker.webp")
        .build();

client.sendStickerAsync("573001112233", sticker).join();
```

## Location

```java
import com.wsclient.api.messages.request.Location;

Location location = Location.builder()
        .latitude("4.710989")
        .longitude("-74.072092")
        .name("Bogotá")     // optional
        .address("Colombia") // optional, only shown if name is present
        .build();

client.sendLocationAsync("573001112233", location).join();
```

Rules: `latitude`/`longitude` required, must be numeric, `latitude` in `[-90, 90]` and `longitude` in `[-180, 180]`.

## Reaction

React to a previous message using its WhatsApp Message ID (WAMID):

```java
import com.wsclient.api.messages.request.Reaction;

Reaction reaction = Reaction.builder()
        .message_id("wamid.HBgL...")
        .emoji("👍")
        .build();

client.sendReactionAsync("573001112233", reaction).join();
```

To **remove** a previously sent reaction, send the same `message_id` with `emoji("")` (empty string, not `null`).

## Contacts

```java
import com.wsclient.api.messages.request.contact.Contact;
import com.wsclient.api.messages.request.contact.ContactName;
import com.wsclient.api.messages.request.contact.ContactPhone;
import java.util.List;

Contact contact = new Contact(
        null,                                                       // addresses
        null,                                                       // birthday
        null,                                                       // emails
        new ContactName("John Doe", "John", "Doe", null, null, null), // name (required)
        null,                                                       // org
        List.of(new ContactPhone("+573001112233", ContactPhone.ContactPhoneType.CELL, null)), // phones
        null);                                                      // urls

client.sendContactsAsync("573001112233", List.of(contact)).join();
```

Rule: the contact list cannot be empty, and each `Contact` must have a `name` with a non-empty `formattedName` (the only required field inside `name`; the rest of `Contact` is optional).
