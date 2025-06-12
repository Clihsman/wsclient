package com.wsclient.api.messages.request.interactive;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveButton
 * 
 * @param type  type
 * @param reply reply
 */
@Builder
@Data
public class InteractiveButton {
    /**
     * only supported if type=reply(for Reply Button)
     */
    @Builder.Default
    private String type = "reply";
    private InteractiveButtonReply reply;
}