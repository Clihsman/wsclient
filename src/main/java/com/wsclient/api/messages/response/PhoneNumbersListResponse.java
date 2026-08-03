package com.wsclient.api.messages.response;

import java.util.List;

import com.wsclient.api.messages.response.template.Paging;

/**
 * Represents the response returned by {@code GET /{waba-id}/phone_numbers}.
 *
 * @param data   The list of phone numbers registered on the WhatsApp
 *               Business Account.
 * @param paging Pagination cursors for fetching additional pages.
 */
public record PhoneNumbersListResponse(List<PhoneNumberResponse> data, Paging paging) {
}
