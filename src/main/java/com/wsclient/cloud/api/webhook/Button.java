package com.wsclient.cloud.api.webhook;

public record Button(
        /**
         * The developer-defined payload for the button when a business account sends
         * interactive messages.
         */
        String payload,
        /**
         * The button text.
         */
        String text) {

}
