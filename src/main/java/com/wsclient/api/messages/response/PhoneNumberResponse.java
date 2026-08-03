package com.wsclient.api.messages.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a phone number registered on a WhatsApp Business Account, as
 * returned by {@code GET /{waba-id}/phone_numbers} or
 * {@code GET /{phone-number-id}}.
 *
 * @param id                     The phone number's ID, used in most other
 *                               API calls.
 * @param displayPhoneNumber     The phone number in international format.
 * @param verifiedName           The business name shown to customers.
 * @param qualityRating          The phone number's messaging quality rating
 *                               (e.g. {@code "GREEN"}, {@code "YELLOW"},
 *                               {@code "RED"}).
 * @param codeVerificationStatus The phone number's verification status
 *                               (e.g. {@code "VERIFIED"}).
 * @param platformType           The platform the number is registered on
 *                               (e.g. {@code "CLOUD_API"}).
 */
public record PhoneNumberResponse(
        String id,
        @JsonProperty("display_phone_number") String displayPhoneNumber,
        @JsonProperty("verified_name") String verifiedName,
        @JsonProperty("quality_rating") String qualityRating,
        @JsonProperty("code_verification_status") String codeVerificationStatus,
        @JsonProperty("platform_type") String platformType) {
}
