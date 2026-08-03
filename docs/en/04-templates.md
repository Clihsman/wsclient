# Templates

## Sending a template message

A `Template` (`com.wsclient.api.messages.request.Template`) requires `name` and `language`, and optionally a list of `components` (header/body/button) with their `parameters`.

```java
import com.wsclient.api.messages.request.*;

Template template = Template.builder()
        .name("order_confirmation")
        .language(Language.builder().code("es_CO").build()) // "policy" is optional; Meta defaults to "deterministic" if omitted
        .components(List.of(
                // Text parameter in the body
                Component.builder()
                        .type(Component.ComponentType.BODY)
                        .parameters(Parameter.builder()
                                .type(Parameter.ParameterType.TEXT)
                                .text("John Doe")
                                .build())
                        .build(),
                // Button with a dynamic quick_reply / url payload
                Component.builder()
                        .type(Component.ComponentType.BUTTON)
                        .parameters(Parameter.builder()
                                .type(Parameter.ParameterType.TEXT)
                                .text("order-123")
                                .build())
                        .build()))
        .build();

client.sendTemplate("573001112233", template).join();
```

`sendTemplate` **does not go through `WhatsAppInputValidator`** (there is no upfront field validation); any formatting error will be reported directly by the Meta API as a `WhatsAppException`.

> **Current limitation:** `Component.parameters` is a single `Parameter`, not a list. If your template needs multiple parameters in the same component (e.g. two text variables in the body), you'll need to send one `Component` per parameter, repeating the same `type`.

### Available parameters (`Parameter.ParameterType`)

| Type | Field to use | Notes |
|---|---|---|
| `TEXT` | `text` | Max. 60 characters in header, 1024 in body (32768 if the template only has a body). |
| `CURRENCY` | `currency` | `Currency(fallbackValue, code, amount1000)` — ISO 4217 code, amount multiplied by 1000. |
| `DATETIME` | `dateTime` | `DateTime` with `fallbackValue` and optional fields (`dayOfWeek`, `yaer` — *actual field typo, not `year`*, `month`, `dayOfMonth`, `hour`, `minute`, `calendar`). |
| `IMAGE` | `image` | A `Media` (`id` or `link`), for image headers. |
| `DOCUMENT` | `document` | A `Media`, PDF only for media-based templates. |

## Listing available templates

`getTamplates()` lives on `WhatsAppService`, not on `WhatsAppClient`, because it needs the **WhatsApp Business Account ID (WABA)**, which `WhatsAppClient.configureWhatsAppApi` does not accept. Configure the service directly:

```java
WhatsAppService service = new WhatsAppServiceImpl();
service.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0",
        "PHONE_NUMBER_ID",
        "WABA_ID",       // businessAccount
        "ACCESS_TOKEN");

TemplatesResponse templates = service.getTamplates().join();
templates.getData().forEach(t -> System.out.println(t.getName() + " - " + t.getStatus()));
```

If you use `WhatsAppClientImpl` for the rest of the app, you can keep sharing the same `WhatsAppService` instance — just make sure you also called `service.configureWhatsAppApi(...)` with the `businessAccount`, since `WhatsAppClientImpl.configureWhatsAppApi` leaves it as `null`.
