package com.wsclient.cloud.api.messages.request.interactive;

import lombok.Builder;

/**
 * InteractiveButton
 * 
 * @param type  type
 * @param reply reply
 */
@Builder
public record InteractiveButton(
        /**
         * only supported if type=reply(for Reply Button)
         */
        String type,
        InteractiveButtonReply reply) {

    /**
     * InteractiveButton
     * 
     * @param type  type
     * @param reply reply
     */
    public InteractiveButton {
        type = "reply";
    }
}
