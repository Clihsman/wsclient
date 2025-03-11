package com.wsclient.cloud.api.messages.request.interactive;

import lombok.Builder;

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
