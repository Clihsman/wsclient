# Primeros pasos

## Requisitos

- Java 21 o superior.
- Maven.
- Un número de WhatsApp Business configurado en Meta (`phoneNumberId`) y un token de acceso (`ACCESS_TOKEN`) con permisos sobre la WhatsApp Cloud API.

## Agregar la dependencia

wsclient no está publicada en Maven Central. Clona el repositorio e instálala en tu repositorio local de Maven:

```sh
git clone https://github.com/Clihsman/wsclient.git
cd wsclient
mvn clean install
```

Luego, en el `pom.xml` de tu proyecto:

```xml
<dependency>
    <groupId>com.wsclient</groupId>
    <artifactId>wsclient</artifactId>
    <version>1.1.0</version>
</dependency>
```

## Inicializar el cliente

El punto de entrada principal es `WhatsAppClient`, que envuelve a `WhatsAppService` (la capa que habla HTTP con la API de Meta):

```java
import com.wsclient.api.services.WhatsAppClient;
import com.wsclient.api.services.WhatsAppClientImpl;
import com.wsclient.api.services.WhatsAppService;
import com.wsclient.api.services.WhatsAppServiceImpl;

WhatsAppService service = new WhatsAppServiceImpl();
WhatsAppClient client = new WhatsAppClientImpl(service);

client.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0", // whatsappApiUrl
        "PHONE_NUMBER_ID",                  // phoneNumberId
        "ACCESS_TOKEN");                    // token
```

`configureWhatsAppApi` valida los tres parámetros antes de continuar y lanza `IllegalArgumentException` si:

- `whatsappApiUrl` es nulo/vacío o no empieza con `http`.
- `phoneNumberId` es nulo/vacío o contiene caracteres que no son dígitos.
- `token` es nulo/vacío.

> **Nota — WhatsApp Business Account ID (WABA):** `WhatsAppClient.configureWhatsAppApi` no recibe el `businessAccount` (WABA ID). Ese dato solo es necesario para listar plantillas (ver [Plantillas](04-templates.md)); si lo necesitas, configura `WhatsAppService` directamente:
> ```java
> service.configureWhatsAppApi(apiUrl, phoneNumberId, businessAccountId, token);
> ```

## El patrón asíncrono

Todos los métodos que llaman a la API devuelven `CompletableFuture<WhatsAppResponse>` (o el tipo de respuesta correspondiente). Internamente:

1. Se valida el input de forma síncrona. Si falla, el `CompletableFuture` se completa inmediatamente con la excepción (`CompletableFuture.failedFuture(...)`) — no se hace ninguna llamada HTTP.
2. Si la validación pasa, la llamada HTTP ocurre dentro de `CompletableFuture.supplyAsync(...)`, y cualquier excepción (de red, o `WhatsAppException` si la API de Meta responde con error) se envuelve en `java.util.concurrent.CompletionException`.

Esto significa que **siempre** debes manejar el resultado como un future, ya sea de forma bloqueante (`.join()`/`.get()`, típico en scripts o tests) o reactiva (`.thenAccept(...)`/`.exceptionally(...)`, recomendado en aplicaciones):

```java
client.sendTextAsync(to, text)
        .thenAccept(response -> log.info("Enviado: {}", response))
        .exceptionally(ex -> {
            Throwable cause = ex.getCause(); // IllegalArgumentException o WhatsAppException
            log.error("Fallo al enviar mensaje", cause);
            return null;
        });
```

Ver [Manejo de errores](08-error-handling.md) para el detalle de qué excepción esperar en cada caso.
