package com.wsclient.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
     * WhatsAppUtils
     */
    private WhatsAppUtils() {
    }

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
            throw new IllegalArgumentException("Invalid message ID format. Expected format: wamid.<base64-encoded-id>");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(parts[1]);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to decode base64 message ID.", e);
        }
    }

    /**
     * Verifies that an incoming webhook request was actually sent by Meta, by
     * validating its {@code X-Hub-Signature-256} header.
     * <p>
     * Meta signs the raw webhook payload with HMAC-SHA256 using the app secret,
     * and sends the resulting hex digest prefixed with {@code "sha256="} in the
     * {@code X-Hub-Signature-256} header. This method recomputes that digest and
     * compares it against the received one using a constant-time comparison.
     * </p>
     *
     * @param payload         The raw (unparsed) webhook request body, exactly as
     *                        received.
     * @param signatureHeader The value of the {@code X-Hub-Signature-256} header
     *                        from the request, or {@code null} if absent.
     * @param appSecret       The Meta App Secret used to sign the payload.
     * @return {@code true} if the signature is present and matches the computed
     *         HMAC; {@code false} if it is missing, malformed, or does not
     *         match.
     * @throws IllegalArgumentException if {@code payload} or {@code appSecret}
     *                                   is {@code null}.
     */
    public static boolean verifyWebhookSignature(String payload, String signatureHeader, String appSecret) {
        if (payload == null || appSecret == null) {
            throw new IllegalArgumentException("Payload and app secret cannot be null.");
        }

        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computed = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            byte[] expected = HexFormat.of().parseHex(signatureHeader.substring("sha256=".length()));
            return MessageDigest.isEqual(computed, expected);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            return false;
        }
    }

}
