package com.wsclient.cloud.api.messages.request.contact;

import com.fasterxml.jackson.annotation.JsonValue;

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
    public enum ContactPhoneType {

        CELL("CELL"),
        MAIN("MAIN"),
        IPHONE("IPHONE"),
        HOME("HOME"),
        WORK("WORK");

        private final String value;

        ContactPhoneType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}