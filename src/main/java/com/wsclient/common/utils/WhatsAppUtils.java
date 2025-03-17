package com.wsclient.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for handling WhatsApp message-related operations.
 * <p>
 * Provides helper methods for extracting and decoding message identifiers.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class is final and cannot be extended.
 * </p>
 */
public final class WhatsAppUtils {

    /**
     * Extracts and decodes the message ID from a given WhatsApp message identifier.
     *
     * @param messageId The full message identifier in the format
     *                  "prefix.encodedId.suffix".
     * @return The decoded message ID.
     * @throws IllegalArgumentException If the messageId format is invalid.
     * @throws IllegalStateException    If the Base64 decoding fails.
     */
    public static String extractMessageId(String messageId) {
        if (messageId == null || messageId.isBlank()) {
            throw new IllegalArgumentException("Message ID cannot be null or empty.");
        }

        String[] parts = messageId.split("\\.");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid message ID format.");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(parts[1]);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Failed to decode message ID.", e);
        }
    }

}
