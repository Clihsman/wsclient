package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a contact entry in the WhatsApp API response.
 * <p>
 * This record holds the contact details provided in the response,
 * including the original input and the formatted WhatsApp ID.
 * </p>
 *
 * @param input The original input used to identify the contact (e.g., a phone
 *              number).
 * @param waId  The formatted WhatsApp ID associated with the contact.
 */
public record ContactData(
        String input,
        @JsonProperty("wa_id") String waId) {
}
