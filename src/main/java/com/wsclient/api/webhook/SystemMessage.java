package com.wsclient.cloud.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This object is added to Webhooks if a user has changed their phone number and
 * if a user’s identity has potentially changed on WhatsApp.
 * 
 * @param body     body
 * @param newWaId  newWaId
 * @param identity identity
 * @param type     type
 * @param user     user
 */
public record SystemMessage(
        /**
         * Describes the system message event. Supported use cases are:
         * <li>
         * - Phone number update: for when a user changes from an old number to a new
         * number.
         * </li>
         * <li>
         * - Identity update: for when a user identity has changed.
         * </li>
         */
        String body,
        /**
         * <strong>
         * Added to Webhooks for phone number updates.
         * </strong>
         * <p>
         * New WhatsApp ID of the customer.
         * </p>
         */
        @JsonProperty("new_wa_id") String newWaId,
        /**
         * Added to Webhooks for identity updates.
         * New WhatsApp ID of the customer.
         */
        String identity,
        /**
         * Supported types are:
         * 
         * <li>
         * - <code>user_changed_number</code>: for a user changed number notification.
         * </li>
         * <li>
         * - <code>user_identity_changed</code>: for user identity changed notification.
         * </li>
         */
        SystemMessageType type,
        /**
         * <strong>
         * Added to Webhooks for identity updates.
         * </strong>
         * <p>
         * The new WhatsApp user ID of the customer.
         * </p>
         */
        String user) {
    /**
     * SystemMessageType
     */
    public enum SystemMessageType {
        /**
         * for a user changed number notification.
         */
        USER_CHANGED_NUMBER("user_changed_number"),
        /**
         * for user identity changed notification.
         */
        USER_IDENTITY_CHANGED("user_identity_changed");

        private final String value;

        SystemMessageType(String value) {
            this.value = value;
        }

        /**
         * getValue
         * 
         * @return value
         */
        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
