package com.wsclient.api.webhook;

import java.util.List;

/**
 * Represents the body of a webhook event received from the WhatsApp API.
 * <p>
 * This record encapsulates the data structure sent by WhatsApp's webhook
 * when an event occurs, such as a new message, status update, or delivery report.
 * </p>
 *
 * @param object The type of event received (e.g., "whatsapp").
 * @param entry  A list of {@link Entry} objects containing detailed event data.
 */
public record WebhookMessageBody(
        String object,
        List<Entry> entry) {
}