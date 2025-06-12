package com.wsclient.api.messages.request.interactive;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveAction
 * 
 * @param button   button
 * @param buttons  buttons
 * @param sections sections
 */
@Builder
@Data
public class InteractiveAction {
    /**
     * <strong>
     * Required for all List Messages.
     * </strong>
     * 
     * <p>
     * The Button content. It cannot be an empty string and must be unique within
     * the message. Does not allow emojis or markdown.
     * </p>
     * 
     * Maximum length: 20 characters.
     */
    private String button;
    /**
     * <strong>
     * Required for Reply Button.
     * </strong>
     * 
     * <p>
     * A button can contain the following parameters:
     * </p>
     * <li>
     * - <code>type</code>: only supported if type=reply(for Reply Button)
     * </li>
     * <li>
     * - <code>title</code>: The Button title. It cannot be an empty string and must
     * be unique
     * within the message. Does not allow emojis or markdown. Maximum length: 20
     * characters.
     * </li>
     * <li>
     * - <code>id</code>: Unique identifier for your button. This ID is returned in
     * the Webhook
     * when the button is clicked by the user. Maximum length: 256 characters.
     * </li>
     * 
     * You can have a maximum of 3 buttons.
     */
    private List<InteractiveButton> buttons;
    /**
     * <strong>
     * Required for List Messages and Multi-Product Messages.
     * </strong>
     * 
     * <p>
     * The array of section objects. There is a minimum of 1 and maximum of 10. For
     * more information, see section object.
     * </p>
     */
    private List<InteractiveSection> sections;

}
