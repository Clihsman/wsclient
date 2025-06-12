package com.wsclient.api.messages.request.interactive;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveBody
 * 
 * @param text text
 */
@Builder
@Data
public class InteractiveBody {
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
        private String text;
}