package com.wsclient.cloud.api.messages.request.interactive;

import lombok.Builder;

/**
 * InteractiveBody
 * 
 * @param text text
 */
@Builder
public record InteractiveBody(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The body content of the message. Emojis and markdown are supported. Links are
         * supported.
         * </p>
         * 
         * Maximum length: 1024 characters
         */
        String text) {

}
