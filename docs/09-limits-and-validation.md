# Límites y validación

`WhatsAppInputValidator` (`com.wsclient.api.validators.WhatsAppInputValidator`) aplica estas reglas **antes** de hacer cualquier llamada a la API — así evitas gastar cuota de la API de WhatsApp con requests que sabes que van a fallar. Los límites numéricos están centralizados en `com.wsclient.api.constants.WhatsAppConstants`.

Nota: `configureWhatsAppApi`/`ConfigValidator` es la excepción — esos métodos lanzan `IllegalArgumentException` directamente en vez de devolverla envuelta en un future (ver [Manejo de errores](08-error-handling.md)).

## Recipiente (`to`)

Aplica a **todos** los métodos de envío: debe ser no nulo y contener solo dígitos (sin `+`, espacios ni guiones), p. ej. `"573001112233"`.

## Texto

| Regla | Valor |
|---|---|
| Longitud mínima | `MESSAGE_MIN_TEXT` = 1 |
| Longitud máxima | `MESSAGE_MAX_TEXT` = 4096 |

## Media (imagen, video, audio, documento, sticker)

- `id` **o** `link` requerido (no ambos vacíos).
- Si hay `link`, debe empezar con `http://` o `https://`.
- `caption`: máximo 1024 caracteres — **no permitida** en audio ni sticker.
- `filename`: **requerido** en documentos (máx. 240 caracteres) — **no permitido** en imagen, video, audio ni sticker.

## Ubicación

| Campo | Regla |
|---|---|
| `latitude` | requerido, numérico, entre `LOCATION_MIN_LATITUDE` (-90) y `LOCATION_MAX_LATITUDE` (90) |
| `longitude` | requerido, numérico, entre `LOCATION_MIN_LONGITUDE` (-180) y `LOCATION_MAX_LONGITUDE` (180) |

## Reacción

- `message_id` requerido, no vacío.
- `emoji` no puede ser `null` (string vacío `""` es válido y significa "quitar reacción").

## Contactos

- La lista debe tener al menos un `Contact`.
- Cada `Contact` requiere `name` con `formattedName` no vacío (es el único campo obligatorio del objeto `Contact`; direcciones, emails, teléfonos, org y urls son opcionales).

## Interactivo — botones

| Regla | Valor |
|---|---|
| `body.text` | requerido |
| Cantidad de botones | entre `INTERACTIVE_MIN_BUTTONS` (1) y `INTERACTIVE_MAX_BUTTONS` (3) |
| Título de botón | 1 a 20 caracteres |
| IDs de botón | únicos dentro del mensaje |

## Interactivo — listas

| Regla | Valor |
|---|---|
| `body.text` | requerido |
| Texto del botón de la lista | requerido, máx. 20 caracteres |
| Cantidad de secciones | 1 a 10 |
| Título de sección | requerido, máx. 24 caracteres |
| Filas por sección | 1 a 10 |
| `id` de fila | requerido, máx. 200 caracteres, **único en todo el mensaje** (no solo en la sección) |
| `title` de fila | requerido, máx. 24 caracteres |
| `description` de fila | opcional, máx. 72 caracteres |

> `WhatsAppConstants` también declara `INTERACTIVE_MIN_LIST_ROWS`, `INTERACTIVE_MAX_LIST_ROWS`, `INTERACTIVE_MAX_ROW_TITLE_LENGTH` e `INTERACTIVE_MAX_ROW_DESCRIPTION_LENGTH` con estos mismos valores, pero el validador de listas actualmente compara contra literales (`10`, `24`, `72`) en vez de esas constantes — los valores coinciden hoy, pero si cambias una constante no cambiará el comportamiento real hasta que el validador se actualice para usarla.

Estos tipos de interactivo se construyen con `InteractiveFactory` — ver [Mensajes interactivos](03-interactive-messages.md).
