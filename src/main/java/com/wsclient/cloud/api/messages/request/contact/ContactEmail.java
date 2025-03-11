package com.wsclient.cloud.api.messages.request.contact;

public record ContactEmail(
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * Email address.
         * </p>
         */
        String name,
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
