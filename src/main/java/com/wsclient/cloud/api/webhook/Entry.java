package com.wsclient.cloud.api.webhook;

public record Entry(
        /**
         * The ID of Whatsapp Business Accounts this Webhook belongs to.
         */
        Integer id,
        /**
         * Changes that triggered the Webhooks call. This field contains an array of
         * change objects.
         */
        Change[] changes) {
}
