package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a message entry in the WhatsApp API response.
 * <p>
 * This record holds the unique identifier of a sent message.
 * </p>
 *
 * @param id The unique identifier of the message.
 * @param message_status The message status.
 */
public record MessageData(String id, @JsonProperty("message_status") String messageStatus) {
}