package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents error details returned by the WhatsApp API.
 * <p>
 * This record encapsulates information about an error that occurred during a
 * request
 * to the WhatsApp API, including the affected messaging product and additional
 * details.
 * </p>
 *
 * @param messagingProduct The messaging product associated with the error
 *                         (e.g., "whatsapp").
 * @param details          Additional details describing the error.
 */
public record ErrorData(
                @JsonProperty("messaging_product") String messagingProduct,
                String details) {

}