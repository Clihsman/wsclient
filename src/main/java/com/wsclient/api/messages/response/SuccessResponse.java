package com.wsclient.api.messages.response;

/**
 * Generic {@code {"success": true}} response shape returned by several
 * WhatsApp Business Management API operations (phone number registration,
 * verification, and QR code deletion).
 *
 * @param success {@code true} if the operation succeeded.
 */
public record SuccessResponse(boolean success) {
}
