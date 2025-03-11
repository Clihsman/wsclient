package com.wsclient.cloud.api.webhook;

public record ButtonReply(
        /**
         * The unique identifier of the button.
         */
        String id,
        /**
         * The title of the button.
         */
        String title) {

}
