# Mensajes interactivos

WhatsApp soporta mensajes con botones de respuesta rápida (`button`) y listas de opciones (`list`). La forma recomendada de construirlos es `InteractiveFactory` (`com.wsclient.api.messages.factory.InteractiveFactory`), un builder fluido que evita armar a mano los objetos `Interactive`/`InteractiveAction`/`InteractiveSection`.

> `InteractiveFactory.build()` no valida el resultado — solo ensambla el objeto. Toda la validación (campos requeridos, límites de longitud, IDs duplicados) ocurre en `WhatsAppInputValidator` cuando llamas a `sendInteractiveAsync`, así que un `Interactive` incompleto se puede construir sin error, pero fallará al enviarlo.

## Botones de respuesta rápida

```java
import com.wsclient.api.messages.factory.InteractiveFactory;
import com.wsclient.api.messages.request.interactive.Interactive;

Interactive buttons = InteractiveFactory.createButton()
        .text("¿Confirmas tu cita?")
        .button("confirm", "Sí")
        .button("cancel", "No")
        .build();

client.sendInteractiveAsync("573001112233", buttons).join();
```

Reglas: `body.text` requerido; entre 1 y 3 botones; cada título de botón entre 1 y 20 caracteres; IDs de botón únicos.

## Listas de opciones

```java
Interactive list = InteractiveFactory.createList()
        .text("Elige una opción:")
        .listButton("Ver opciones") // texto del botón que abre la lista, máx. 20 caracteres
        .section("Menú principal")
        .row("1", "Opción A", "Descripción A") // id, título, descripción (opcional)
        .row("2", "Opción B")
        .section("Otra sección")
        .row("3", "Opción C")
        .build();

client.sendInteractiveAsync("573001112233", list).join();
```

Reglas: `body.text` requerido; el botón de la lista es requerido (máx. 20 caracteres); hasta 10 secciones, cada una con título (máx. 24 caracteres) y entre 1 y 10 filas; cada fila requiere `id` (máx. 200 caracteres) y `title` (máx. 24 caracteres); `description` es opcional (máx. 72 caracteres); los IDs de fila deben ser únicos **en todo el mensaje**, no solo dentro de una sección.

## Construcción manual (sin el factory)

También puedes construir el `Interactive` directamente si necesitas más control:

```java
import com.wsclient.api.messages.request.interactive.*;

Interactive interactive = Interactive.builder()
        .type(Interactive.InteractiveType.BUTTON)
        .body(InteractiveBody.builder().text("¿Confirmas?").build())
        .action(InteractiveAction.builder()
                .buttons(List.of(
                        InteractiveButton.builder()
                                .reply(InteractiveButtonReply.builder().id("confirm").title("Sí").build())
                                .build()))
                .build())
        .build();
```

## Otros tipos de interactivo (no soportados aún)

`Interactive.InteractiveType` también declara `PRODUCT`, `PRODUCT_LIST` y `CTA_URL`, pero **`InteractiveFactory` y `WhatsAppInputValidator` solo dan soporte completo a `BUTTON` y `LIST`**. Los tipos de catálogo/CTA URL quedan fuera del alcance actual de la librería.
