package com.wsclient.cloud.api.webhook;

public record Identity(
        /**
         * State of acknowledgment for latest <code>user_identity_changed</code> system
         * notification.
         */
        String acknowledged,
        /**
         * The timestamp of when the WhatsApp Business API detected the user potentially
         * changed.
         */
        String created_timestamp,
        /**
         * Identifier for the latest <code>user_identity_changed</code> system notification.
         */
        String hash) {

}
