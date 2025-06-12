package com.wsclient.api.messages.request.interactive;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveButtonReply
 * 
 * @param title title
 * @param id    id
 */
@Builder
@Data
public class InteractiveButtonReply {

        /**
         * The Button title. It cannot be an empty string and must be unique within the
         * message. Does not allow emojis or markdown. Maximum length: 20 characters.
         */
        private String title;
        /**
         * Unique identifier for your button. This ID is returned in the Webhook when
         * the button is clicked by the user. Maximum length: 256 characters.
         */
        private String id;

}