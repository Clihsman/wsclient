package com.wsclient.cloud.api.messages.request;

import lombok.Builder;

/**
 * The Language Object contains the following fields
 * 
 * @param policy policy
 * @param code code
 */
@Builder
public record Language(
        /**
         * <strong>
         * Optional.
         * </strong>
         * <p>
         * For more information, see Language Policy Options.
         * </p>
         * Default (and only supported value): deterministic
         */
        String policy,
        /**
         * <strong>
         * Required.
         * </strong>
         * <p>
         * The code of the language or locale to use. This field accepts both language
         * (for example, ‘en’) and language_locale (for example, ‘en_US’) formats.
         * For more information regarding all codes, see
         * 
         * @see <a href=
         *      "https://developers.facebook.com/docs/whatsapp/api/messages/message-templates#supported-languages">Supported
         *      Languages.</a>
         *      </p>
         */
        String code) {

}
