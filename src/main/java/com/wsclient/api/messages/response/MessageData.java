package com.wsclient.api.messages.response;

/**
 * Represents a message entry in the WhatsApp API response.
 * <p>
 * This record holds the unique identifier of a sent message.
 * </p>
 *
 * @param id The unique identifier of the message.
 */
public record MessageData(String id) {
}