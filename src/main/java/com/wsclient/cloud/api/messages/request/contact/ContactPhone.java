package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ContactPhone
 * 
 * @param phone phone
 * @param type  type
 * @param ws_id ws_id
 */
public record ContactPhone(
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Automatically populated with the wa_id value as a formatted phone number.
         * </p>
         */
        String phone,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Standard Values: <code>CELL</code>,
         * <code>MAIN</code>, <code>IPHONE</code>,
         * <code>HOME</code>, <code>WORK</code>
         * </p>
         */
        ContactPhoneType type,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * WhatsApp ID.
         * </p>
         */
        String ws_id) {
    /**
     * ContactPhoneType
     */
    public enum ContactPhoneType {
        /**
         * CELL
         */
        CELL("CELL"),
        /**
         * MAIN
         */
        MAIN("MAIN"),
        /**
         * IPHONE
         */
        IPHONE("IPHONE"),
        /**
         * HOME
         */
        HOME("HOME"),
        /**
         * WORK
         */
        WORK("WORK");

        private final String value;

        ContactPhoneType(String value) {
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