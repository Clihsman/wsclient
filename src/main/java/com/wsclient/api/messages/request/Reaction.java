package com.wsclient.api.messages.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Reaction Object consists of a message ID and a emoji.
 * 
 * @param message_id message_id
 * @param emoji      emoji
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    /**
     * <strong>
     * Required.
     * </strong>
     * <p>
     * Specifies the WhatsApp message ID (WAMID) that this reaction is being sent
     * to.
     * </p>
     * You cannot send a reaction to a message_id that previously sent or received
     * reaction messages.
     */
    private String message_id;
    /*
     * <strong>
     * Required.
     * </strong>
     * <p>
     * The emoji used for the reaction.
     * </p>
     * </p>
     * All emojis are supported, however only one emoji can be sent in a reaction
     * message. Set this value to "" (empty string) to remove the reaction.
     * Unicode is not supported. However, unicode values can be Java or
     * JavaScript-escape encoded.
     * </p>
     */
    private String emoji;
}
