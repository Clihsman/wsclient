package com.wsclient.api.templates.response;

/**
 * Response returned by the WhatsApp Cloud API after editing or deleting a
 * message template.
 *
 * @param success {@code true} if the operation succeeded.
 */
public record TemplateActionResponse(boolean success) {
}
