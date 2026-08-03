# Templates

## Sending a template message

The recommended way to build a `Template` is `TemplateFactory` (`com.wsclient.api.messages.factory.TemplateFactory`), a fluent builder analogous to `InteractiveFactory` (see [Interactive messages](03-interactive-messages.md)):

```java
import com.wsclient.api.messages.factory.TemplateFactory;
import com.wsclient.api.messages.request.Template;

Template template = TemplateFactory.create("order_confirmation", "es_CO")
        .headerText("Order confirmed")
        .bodyText("John Doe")    // first body parameter
        .bodyText("order-123")   // second parameter of the same body
        .buttonText("order-123") // dynamic button parameter
        .build();

client.sendTemplate("573001112233", template).join();
```

Each call to `bodyText`/`headerText`/etc. appends a parameter to the corresponding component's list (`header`, `body`, or `button`) — it does not create a new component per call. Components are always assembled in the order `header`, `body`, `button`, regardless of the order you called the methods, and a component only appears in the final `Template` if you added at least one parameter to it.

`sendTemplate` **does not go through `WhatsAppInputValidator`** (there is no upfront field validation); any formatting error will be reported directly by the Meta API as a `WhatsAppException`.

> **Known limitation:** `buttonText(...)` appends parameters to a single `BUTTON` component. The `Component` model has no `sub_type` or `index`, so this factory cannot target a specific button when a template has more than one.

### Available parameters

Each content type has its own method on `TemplateFactory` (`headerText`/`bodyText`/`buttonText`, `headerCurrency`/`bodyCurrency`, `headerDateTime`/`bodyDateTime`, `headerImage`/`bodyImage`, `headerDocument`/`bodyDocument`):

| Type | Notes |
|---|---|
| Text | Max. 60 characters in header, 1024 in body (32768 if the template only has a body). |
| Currency (`Currency`) | `Currency(fallbackValue, code, amount1000)` — ISO 4217 code, amount multiplied by 1000. |
| Date/time (`DateTime`) | `fallbackValue` required; optional fields: `dayOfWeek`, `year`, `month`, `dayOfMonth`, `hour`, `minute`, `calendar`. |
| Image (`Media`) | `id` or `link`, for image headers. |
| Document (`Media`) | `id` or `link`, PDF only for media-based templates. |

### Manual construction (without the factory)

```java
import com.wsclient.api.messages.request.*;
import java.util.List;

Template template = Template.builder()
        .name("order_confirmation")
        .language(Language.builder().code("es_CO").build()) // "policy" is optional; Meta defaults to "deterministic" if omitted
        .components(List.of(
                Component.builder()
                        .type(Component.ComponentType.BODY)
                        .parameters(List.of(
                                Parameter.builder().type(Parameter.ParameterType.TEXT).text("John Doe").build(),
                                Parameter.builder().type(Parameter.ParameterType.TEXT).text("order-123").build()))
                        .build()))
        .build();
```

`Component.parameters` is always a list — the WhatsApp API expects an array even when the component only has one parameter.

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
