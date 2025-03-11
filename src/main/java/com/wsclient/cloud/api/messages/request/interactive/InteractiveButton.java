package com.wsclient.cloud.api.messages.request.interactive;

import lombok.Builder;

@Builder
public record InteractiveButton(
        /**
         * only supported if type=reply(for Reply Button)
         */
        String type,
        InteractiveButtonReply reply) {
    public InteractiveButton {
        type = "reply";
    }
}
