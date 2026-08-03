# Limits and validation

`WhatsAppInputValidator` (`com.wsclient.api.validators.WhatsAppInputValidator`) applies these rules **before** making any API call — saving you from spending WhatsApp API quota on requests you already know will fail. Numeric limits are centralized in `com.wsclient.api.constants.WhatsAppConstants`.

Note: `configureWhatsAppApi`/`ConfigValidator` is the exception — those methods throw `IllegalArgumentException` directly instead of returning it wrapped in a future (see [Error handling](08-error-handling.md)).

## Recipient (`to`)

Applies to **all** sending methods: must be non-null and contain only digits (no `+`, spaces, or dashes), e.g. `"573001112233"`.

## Text

| Rule | Value |
|---|---|
| Minimum length | `MESSAGE_MIN_TEXT` = 1 |
| Maximum length | `MESSAGE_MAX_TEXT` = 4096 |

## Media (image, video, audio, document, sticker)

- `id` **or** `link` required (both cannot be blank).
- If `link` is present, it must start with `http://` or `https://`.
- `caption`: max 1024 characters — **not allowed** for audio or sticker.
- `filename`: **required** for documents (max. 240 characters) — **not allowed** for image, video, audio, or sticker.

## Location

| Field | Rule |
|---|---|
| `latitude` | required, numeric, between `LOCATION_MIN_LATITUDE` (-90) and `LOCATION_MAX_LATITUDE` (90) |
| `longitude` | required, numeric, between `LOCATION_MIN_LONGITUDE` (-180) and `LOCATION_MAX_LONGITUDE` (180) |

## Reaction

- `message_id` required, non-blank.
- `emoji` cannot be `null` (an empty string `""` is valid and means "remove reaction").

## Contacts

- The list must contain at least one `Contact`.
- Each `Contact` requires a `name` with a non-empty `formattedName` (the only mandatory field of the `Contact` object; addresses, emails, phones, org, and urls are optional).

## Templates

| Rule | Value |
|---|---|
| `name` | required, non-blank |
| `language` | required, with a non-blank `code` |
| Text parameter in `header` | max. `TEMPLATE_HEADER_TEXT_MAX_LENGTH` (60) characters |
| Text parameter in `body`/`button` | max. `TEMPLATE_BODY_TEXT_MAX_LENGTH` (1024) characters |

How many parameters the template already registered with Meta actually expects is not validated — see [Templates](04-templates.md).

## Interactive — buttons

| Rule | Value |
|---|---|
| `body.text` | required |
| Number of buttons | between `INTERACTIVE_MIN_BUTTONS` (1) and `INTERACTIVE_MAX_BUTTONS` (3) |
| Button title | 1 to 20 characters |
| Button IDs | unique within the message |

## Interactive — lists

| Rule | Value |
|---|---|
| `body.text` | required |
| List button text | required, max. 20 characters |
| Number of sections | `INTERACTIVE_MIN_SECTIONS` (1) to `INTERACTIVE_MAX_SECTIONS` (10) |
| Section title | required, max. `INTERACTIVE_MAX_SECTION_TITLE_LENGTH` (24) characters |
| Rows per section | `INTERACTIVE_MIN_LIST_ROWS` (1) to `INTERACTIVE_MAX_LIST_ROWS` (10) |
| Row `id` | required, max. 200 characters, **unique across the whole message** (not just within the section) |
| Row `title` | required, max. `INTERACTIVE_MAX_ROW_TITLE_LENGTH` (24) characters |
| Row `description` | optional, max. `INTERACTIVE_MAX_ROW_DESCRIPTION_LENGTH` (72) characters |

## Interactive — single product

| Rule | Value |
|---|---|
| `body.text` | **optional** (unlike the other types) |
| `catalogId` | required |
| `productRetailerId` | required |

## Interactive — multi-product

| Rule | Value |
|---|---|
| `body.text` | required |
| `catalogId` | required |
| Number of sections | `INTERACTIVE_MIN_SECTIONS` (1) to `INTERACTIVE_MAX_SECTIONS` (10) |
| Products per section | at least 1, each with a non-blank `productRetailerId` |

## Interactive — CTA URL button

| Rule | Value |
|---|---|
| `body.text` | required |
| `parameters["display_text"]` | required, non-blank |
| `parameters["url"]` | required, non-blank, must be `http(s)://` |

These interactive types are built with `InteractiveFactory` — see [Interactive messages](03-interactive-messages.md).
