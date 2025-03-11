package com.wsclient.cloud.api.messages.request.contact;

public record ContactUrl(
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * The URL.
         * </p>
         */
        String url,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Standard Values: <code>HOME</code>, <code>WORK</code>
         * </p>
         */
        ContactStandardType type) {
}
