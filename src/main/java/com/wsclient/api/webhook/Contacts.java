package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The contacts array of objects, nested within the Value object alongside
 * messages — one entry per message, describing who sent it.
 *
 * <p>
 * For a brand-new contact on a given business phone number, WhatsApp may
 * omit the customer's phone number from {@link Messages#from()} and send a
 * business-scoped identifier there instead; {@code wa_id} here is the
 * reliable way to address that same contact (e.g. to send a reply), even
 * while {@code from} isn't a phone number yet. After the contact has
 * exchanged enough messages, WhatsApp starts sending their phone number in
 * both fields.
 *
 * @param profile profile
 * @param waId    waId
 * @param userId  userId
 */
public record Contacts(
        /**
         * The customer's profile information.
         */
        Profile profile,
        /**
         * The WhatsApp ID of the customer — usable as the {@code to} value when
         * sending messages back, whether or not it happens to be their phone
         * number.
         */
        @JsonProperty("wa_id") String waId,
        /**
         * Sent instead of {@code wa_id} for a contact that is new to this business
         * phone number — see {@link Messages#fromUserId()}, which carries the same
         * value for the matching message.
         */
        @JsonProperty("user_id") String userId) {

    /**
     * Profile
     *
     * @param name name
     */
    public record Profile(
            /**
             * The customer's name, as configured in their WhatsApp profile.
             */
            String name) {
    }
}
