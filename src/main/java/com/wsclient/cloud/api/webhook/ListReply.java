package com.wsclient.cloud.api.webhook;

public record ListReply(
        /**
         * The unique identifier (ID) of the selected row.
         */
        String id,
        /**
         * The title of the selected row.
         */
        String title,
        /**
         * The description of the selected row.
         */
        String description) {
}
