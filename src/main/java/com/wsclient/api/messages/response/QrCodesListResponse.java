package com.wsclient.api.messages.response;

import java.util.List;

/**
 * Represents the response returned by
 * {@code GET /{phone-number-id}/message_qrdls}.
 *
 * @param data The list of QR codes created for the phone number.
 */
public record QrCodesListResponse(List<QrCodeResponse> data) {
}
