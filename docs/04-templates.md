# Plantillas (templates)

## Enviar un mensaje de plantilla

Un `Template` (`com.wsclient.api.messages.request.Template`) requiere `name` y `language`, y opcionalmente una lista de `components` (header/body/button) con sus `parameters`.

```java
import com.wsclient.api.messages.request.*;

Template template = Template.builder()
        .name("order_confirmation")
        .language(Language.builder().code("es_CO").build()) // "policy" es opcional; Meta usa "deterministic" si se omite
        .components(List.of(
                // Parámetro de texto en el body
                Component.builder()
                        .type(Component.ComponentType.BODY)
                        .parameters(Parameter.builder()
                                .type(Parameter.ParameterType.TEXT)
                                .text("Juan Pérez")
                                .build())
                        .build(),
                // Botón de tipo quick_reply / url con payload dinámico
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

`sendTemplate` **no pasa por `WhatsAppInputValidator`** (no hay validación de campos previa); cualquier error de formato lo reportará directamente la API de Meta como un `WhatsAppException`.

> **Limitación actual:** `Component.parameters` es un único `Parameter`, no una lista. Si tu plantilla necesita varios parámetros en el mismo componente (por ejemplo, dos variables de texto en el body), tendrás que enviar un `Component` por cada parámetro repitiendo el mismo `type`.

### Parámetros disponibles (`Parameter.ParameterType`)

| Tipo | Campo a usar | Notas |
|---|---|---|
| `TEXT` | `text` | Máx. 60 caracteres en header, 1024 en body (32768 si el template solo tiene body). |
| `CURRENCY` | `currency` | `Currency(fallbackValue, code, amount1000)` — código ISO 4217, monto multiplicado por 1000. |
| `DATETIME` | `dateTime` | `DateTime` con `fallbackValue` y campos opcionales (`dayOfWeek`, `yaer` — *typo existente en el campo, no `year`*, `month`, `dayOfMonth`, `hour`, `minute`, `calendar`). |
| `IMAGE` | `image` | Un `Media` (`id` o `link`), para headers de imagen. |
| `DOCUMENT` | `document` | Un `Media`, solo PDF para templates media-based. |

## Listar plantillas disponibles

`getTamplates()` vive en `WhatsAppService`, no en `WhatsAppClient`, porque necesita el **WhatsApp Business Account ID (WABA)**, que `WhatsAppClient.configureWhatsAppApi` no recibe. Configura el servicio directamente:

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

Si usas `WhatsAppClientImpl` para el resto de la app, puedes seguir compartiendo la misma instancia de `WhatsAppService` — solo asegúrate de haber llamado también a `service.configureWhatsAppApi(...)` con el `businessAccount`, ya que `WhatsAppClientImpl.configureWhatsAppApi` lo deja en `null`.
