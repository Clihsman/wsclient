package com.wsclient.api.messages.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response received from the WhatsApp API.
 * <p>
 * This record encapsulates the details of a WhatsApp API response, including
 * the messaging product,
 * contact information, and message details.
 * </p>
 *
 * @param messagingProduct The type of messaging product (e.g., "whatsapp").
 * @param contacts         A list of contacts involved in the communication.
 * @param messages         A list of messages sent or received.
 */
public record WhatsAppResponse(
        @JsonProperty("messaging_product") String messagingProduct,
        List<ContactData> contacts,
        List<MessageData> messages) {
}
