package com.wsclient.cloud.api.messages.response;

/**
 * Represents a message entry in the WhatsApp API response.
 * <p>
 * This record holds the unique identifier of a sent message.
 * </p>
 *
 * @param waId The unique identifier of the message.
 */
public record MessageData(String waId) {
}