package com.wsclient.api.webhook;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Value
 * 
 * @param messagingProduct messagingProduct
 * @param metadata         metadata
 * @param messages         messages
 * @param statuses         statuses
 * @param contacts         contacts
 */
public record Value(
                /**
                 * The messaging service used for Webhooks. For WhatsApp messages, this value
                 * needs to be set to <code>“whatsapp”</code>.
                 */
                @JsonProperty("messaging_product") String messagingProduct,
                /**
                 * The metadata about your phone number.
                 */
                MetaData metadata,
                /**
                 * An array of message objects. Added to Webhooks for incoming message
                 * notifications.
                 */
                List<Messages> messages,
                /**
                 * An array of message status objects. Added to Webhooks for message status
                 * update.
                 */
                List<Statuses> statuses,
                /**
                 * An array of contact objects, one per entry in {@code messages} — see
                 * {@link Contacts}. Added to Webhooks for incoming message notifications.
                 */
                List<Contacts> contacts) {

}
