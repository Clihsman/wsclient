# Documentación de wsclient

**Idioma:** Español | [English](../en/README.md)

wsclient es una librería Java para interactuar con la WhatsApp Cloud API (Meta Graph API): envío de mensajes, subida de media, gestión del perfil de negocio y manejo de webhooks.

Esta carpeta documenta cómo **usar** la librería. Para entender su arquitectura interna (paquetes, capas, convenciones de código), consulta el [`CLAUDE.md`](../../CLAUDE.md) en la raíz del repositorio.

## Índice

1. [Primeros pasos](01-getting-started.md) — dependencia, configuración del cliente, patrón asíncrono.
2. [Envío de mensajes](02-sending-messages.md) — texto, imagen, video, audio, documento, sticker, ubicación, reacción, contactos.
3. [Mensajes interactivos](03-interactive-messages.md) — botones, listas, producto único/múltiple y botón CTA URL con `InteractiveFactory`.
4. [Plantillas (templates)](04-templates.md) — envío, listado, creación, edición y eliminación de plantillas.
5. [Subida de media](05-media.md) — subir, consultar y eliminar archivos, y reutilizar su `id` en los mensajes.
6. [Perfil de negocio](06-business-profile.md) — leer y actualizar el Business Profile.
7. [Webhooks](07-webhooks.md) — suscribir la app, verificar la firma de Meta, parsear eventos entrantes.
8. [Manejo de errores](08-error-handling.md) — `WhatsAppException`, `CompletionException` y validación.
9. [Límites y validación](09-limits-and-validation.md) — constantes y reglas que aplica la librería antes de llamar a la API.
10. [Gestión de cuenta de negocio](10-business-management.md) — números de teléfono y códigos QR.
11. [OAuth](11-oauth.md) — App Access Token, intercambio de código y tokens de larga duración.

## Ejemplo mínimo

```java
WhatsAppService service = new WhatsAppServiceImpl();
WhatsAppClient client = new WhatsAppClientImpl(service);

client.configureWhatsAppApi(
        "https://graph.facebook.com/v20.0",
        "PHONE_NUMBER_ID",
        "ACCESS_TOKEN");

Text text = Text.builder().body("Hola desde wsclient!").build();

client.sendTextAsync("573001112233", text)
        .thenAccept(response -> System.out.println(response.messages()))
        .exceptionally(ex -> {
            System.err.println("Error enviando mensaje: " + ex.getCause().getMessage());
            return null;
        });
```
