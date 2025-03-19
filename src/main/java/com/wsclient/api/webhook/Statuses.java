package com.wsclient.api.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The <code>statuses</code> object informs you of the status of messages
 * between you, users,
 * and/or groups.
 * 
 * @param id          id
 * @param recipientId recipientId
 * @param status      status
 * @param timestamp   timestamp
 * @param type        type
 */
public record Statuses(
        /**
         * The message ID.
         */
        String id,
        /**
         * The WhatsApp ID of the recipient.
         */
        @JsonProperty("recipient_id") String recipientId,
        /**
         * The status of the message. Valid values are: <code>read</code>,
         * <code>delivered</code>, <code>sent</code>, <code>failed</code>,
         * or <code>deleted</code>.
         * <p>
         * For more information, see All Possible Message Statuses.
         * </p>
         */
        Status status,
        /**
         * The timestamp of the status message.
         */
        String timestamp,
        /**
         * The type of entity this status object is about. Currently, the only available
         * option is <code>"message"</code>.
         * <p>
         * This object is only available for the On-Premises implementation of the API.
         * Cloud API developers will not receive this field.
         * </p>
         */
        String type) {
    /**
     * Status
     */
    public enum Status {
        /**
         * READ
         */
        READ("read"),
        /**
         * DELIVERED
         */
        DELIVERED("delivered"),
        /**
         * SENT
         */
        SENT("sent"),
        /**
         * FAILED
         */
        FAILED("failed"),
        /**
         * DELETED
         */
        DELETED("deleted");

        private final String value;

        Status(String value) {
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
