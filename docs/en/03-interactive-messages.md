# Interactive messages

WhatsApp supports quick-reply button messages (`button`) and option lists (`list`). The recommended way to build them is `InteractiveFactory` (`com.wsclient.api.messages.factory.InteractiveFactory`), a fluent builder that avoids assembling `Interactive`/`InteractiveAction`/`InteractiveSection` objects by hand.

> `InteractiveFactory.build()` does not validate the result — it only assembles the object. All validation (required fields, length limits, duplicate IDs) happens in `WhatsAppInputValidator` when you call `sendInteractiveAsync`, so an incomplete `Interactive` can be built without error but will fail when sent.

## Quick-reply buttons

```java
import com.wsclient.api.messages.factory.InteractiveFactory;
import com.wsclient.api.messages.request.interactive.Interactive;

Interactive buttons = InteractiveFactory.createButton()
        .text("Do you confirm your appointment?")
        .button("confirm", "Yes")
        .button("cancel", "No")
        .build();

client.sendInteractiveAsync("573001112233", buttons).join();
```

Rules: `body.text` required; between 1 and 3 buttons; each button title between 1 and 20 characters; unique button IDs.

## Option lists

```java
Interactive list = InteractiveFactory.createList()
        .text("Choose an option:")
        .listButton("View options") // text of the button that opens the list, max. 20 characters
        .section("Main menu")
        .row("1", "Option A", "Description A") // id, title, description (optional)
        .row("2", "Option B")
        .section("Another section")
        .row("3", "Option C")
        .build();

client.sendInteractiveAsync("573001112233", list).join();
```

Rules: `body.text` required; the list button is required (max. 20 characters); up to 10 sections, each with a title (max. 24 characters) and between 1 and 10 rows; each row requires `id` (max. 200 characters) and `title` (max. 24 characters); `description` is optional (max. 72 characters); row IDs must be unique **across the whole message**, not just within a section.

## Manual construction (without the factory)

You can also build the `Interactive` directly if you need more control:

```java
import com.wsclient.api.messages.request.interactive.*;

Interactive interactive = Interactive.builder()
        .type(Interactive.InteractiveType.BUTTON)
        .body(InteractiveBody.builder().text("Do you confirm?").build())
        .action(InteractiveAction.builder()
                .buttons(List.of(
                        InteractiveButton.builder()
                                .reply(InteractiveButtonReply.builder().id("confirm").title("Yes").build())
                                .build()))
                .build())
        .build();
```

## Other interactive types (not supported yet)

`Interactive.InteractiveType` also declares `PRODUCT`, `PRODUCT_LIST` and `CTA_URL`, but **`InteractiveFactory` and `WhatsAppInputValidator` only fully support `BUTTON` and `LIST`**. Catalog/CTA URL types are outside the library's current scope.
