package com.wsclient.api.messages.request.interactive;

/**
 * InteractiveFooter
 * 
 * @param text text
 */
public record InteractiveFooter(
                /**
                 * <strong>
                 * Required if the <code>footer</code> object is present.
                 * </strong>
                 * 
                 * <p>
                 * The footer content of the message. Emojis and markdown are supported. Links
                 * are supported.
                 * </p>
                 * 
                 * Maximum length: 60 characters.
                 */
                String text) {
}
