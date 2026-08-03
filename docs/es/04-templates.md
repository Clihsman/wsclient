# Plantillas (templates)

## Enviar un mensaje de plantilla

La forma recomendada de construir un `Template` es `TemplateFactory` (`com.wsclient.api.messages.factory.TemplateFactory`), un builder fluido análogo a `InteractiveFactory` (ver [Mensajes interactivos](03-interactive-messages.md)):

```java
import com.wsclient.api.messages.factory.TemplateFactory;
import com.wsclient.api.messages.request.Template;

Template template = TemplateFactory.create("order_confirmation", "es_CO")
        .headerText("Pedido confirmado")
        .bodyText("Juan Pérez")   // primer parámetro del body
        .bodyText("order-123")   // segundo parámetro del mismo body
        .buttonText("order-123") // parámetro dinámico del botón
        .build();

client.sendTemplate("573001112233", template).join();
```

Cada llamada a `bodyText`/`headerText`/etc. agrega un parámetro a la lista del componente correspondiente (`header`, `body` o `button`) — no crea un componente nuevo por cada llamada. Los componentes se ensamblan siempre en el orden `header`, `body`, `button`, sin importar en qué orden los hayas llamado, y un componente solo aparece en el `Template` final si le agregaste al menos un parámetro.

`sendTemplate` valida el `to`, el `name`/`language` de la plantilla y la longitud de los parámetros de texto por componente (60 caracteres en `header`, 1024 en `body`/`button`) antes de llamar a la API — ver [Límites y validación](09-limits-and-validation.md). No valida cuántos parámetros espera la plantilla registrada en Meta ni el resto de reglas específicas de cada plantilla; esos errores los reportará la API como `WhatsAppException`.

> **Limitación conocida:** `buttonText(...)` agrega parámetros a un único componente `BUTTON`. El modelo `Component` no tiene `sub_type` ni `index`, así que esta factory no puede dirigir un parámetro a un botón específico cuando la plantilla tiene más de uno.

### Parámetros disponibles

Cada tipo de contenido tiene su método en `TemplateFactory` (`headerText`/`bodyText`/`buttonText`, `headerCurrency`/`bodyCurrency`, `headerDateTime`/`bodyDateTime`, `headerImage`/`bodyImage`, `headerDocument`/`bodyDocument`):

| Tipo | Notas |
|---|---|
| Texto | Máx. 60 caracteres en header, 1024 en body (32768 si el template solo tiene body). |
| Moneda (`Currency`) | `Currency(fallbackValue, code, amount1000)` — código ISO 4217, monto multiplicado por 1000. |
| Fecha/hora (`DateTime`) | `fallbackValue` requerido; campos opcionales: `dayOfWeek`, `year`, `month`, `dayOfMonth`, `hour`, `minute`, `calendar`. |
| Imagen (`Media`) | `id` o `link`, para headers de imagen. |
| Documento (`Media`) | `id` o `link`, solo PDF para templates media-based. |

### Construcción manual (sin el factory)

```java
import com.wsclient.api.messages.request.*;
import java.util.List;

Template template = Template.builder()
        .name("order_confirmation")
        .language(Language.builder().code("es_CO").build()) // "policy" es opcional; Meta usa "deterministic" si se omite
        .components(List.of(
                Component.builder()
                        .type(Component.ComponentType.BODY)
                        .parameters(List.of(
                                Parameter.builder().type(Parameter.ParameterType.TEXT).text("Juan Pérez").build(),
                                Parameter.builder().type(Parameter.ParameterType.TEXT).text("order-123").build()))
                        .build()))
        .build();
```

`Component.parameters` es siempre una lista — la API de WhatsApp espera un array incluso cuando el componente solo tiene un parámetro.

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
