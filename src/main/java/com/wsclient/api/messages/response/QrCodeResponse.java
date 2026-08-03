package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a WhatsApp message shortlink (QR code), as returned by the
 * {@code /{phone-number-id}/message_qrdls} endpoints.
 *
 * @param code             The QR code's unique identifier.
 * @param prefilledMessage The message pre-filled in the chat when a customer
 *                         scans the code or opens the deep link.
 * @param deepLinkUrl      The {@code wa.me} deep link associated with the
 *                         code.
 * @param qrImageUrl       A URL to the QR code image, present only when the
 *                         code was created with {@code generate_qr_image}
 *                         set.
 */
public record QrCodeResponse(
        String code,
        @JsonProperty("prefilled_message") String prefilledMessage,
        @JsonProperty("deep_link_url") String deepLinkUrl,
        @JsonProperty("qr_image_url") String qrImageUrl) {
}
