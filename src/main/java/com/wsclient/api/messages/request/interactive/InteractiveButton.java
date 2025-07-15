package com.wsclient.api.messages.request.interactive;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveButton
 * 
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