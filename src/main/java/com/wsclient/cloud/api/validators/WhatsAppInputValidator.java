package com.wsclient.cloud.api.validators;

import java.util.List;
import java.util.Objects;

import com.wsclient.cloud.api.exceptions.WhatsAppException;
import com.wsclient.cloud.api.messages.request.Text;
import com.wsclient.cloud.api.messages.request.interactive.Interactive;
import com.wsclient.cloud.api.messages.request.interactive.Interactive.InteractiveType;
import com.wsclient.cloud.api.messages.request.interactive.InteractiveButton;

import static com.wsclient.cloud.api.constants.WhatsAppConstants.*;

/**
 * A utility class for validating input parameters related to WhatsApp
 * messaging.
 * <p>
 * This class provides methods to validate the recipient phone number, message
 * body,
 * interactive messages, and buttons before sending a WhatsApp message.
 * </p>
 * <p>
 * All validation methods return a {@link WhatsAppException} if the input is
 * invalid;
 * otherwise, they return {@code null}.
 * </p>
 *
 * <h2>Example Usage:</h2>
 * 
 * <pre>
 * {@code
 * WhatsAppException exception = WhatsAppClientValidator.validateMessageInput(to, text);
 * if (exception != null) {
 *     throw exception;
 * }
 * }
 * </pre>
 *
 * @author Clisman Isaac Iscala
 * @version 1.0
 * @since 2025-03-10
 */
public final class WhatsAppInputValidator {

    /**
     * Validates the input parameters for sending a WhatsApp message.
     *
     * @param to   The recipient's phone number as a string. It must contain only
     *             digits.
     * @param text The text object containing the message body.
     * @return A {@link WhatsAppException} if any validation fails; otherwise,
     *         returns {@code null}.
     * @throws WhatsAppException If any of the input parameters are invalid.
     */
    public static WhatsAppException validateMessageInput(String to, Text text) {

        if (Objects.isNull(to)) {
            return new WhatsAppException("Recipient number cannot be null.");
        }

        if (Objects.isNull(text)) {
            return new WhatsAppException("Text object cannot be null.");
        }

        if (Objects.isNull(text.body())) {
            return new WhatsAppException("Message body cannot be null.");
        }

        final String message = text.body().trim();

        if (!to.matches("\\d+")) {
            return new WhatsAppException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (message.length() < MESSAGE_MIN_TEXT) {
            return new WhatsAppException(
                    String.format("Message is too short. Minimum length allowed is %d characters.",
                            MESSAGE_MIN_TEXT));
        }

        if (message.length() > MESSAGE_MAX_TEXT) {
            return new WhatsAppException(
                    String.format("Message exceeds max length of %d characters.",
                            MESSAGE_MAX_TEXT));
        }

        return null;
    }

    /**
     * Validates the input parameters for sending an interactive WhatsApp message.
     *
     * @param to          The recipient's phone number as a string. It must contain
     *                    only digits.
     * @param interactive The interactive message object containing the message type
     *                    and actions.
     * @return A {@link WhatsAppException} if any validation fails; otherwise,
     *         returns {@code null}.
     * @throws WhatsAppException If any of the input parameters are invalid.
     */
    public static WhatsAppException validateInteractiveInput(String to, Interactive interactive) {
        if (Objects.isNull(to)) {
            return new WhatsAppException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new WhatsAppException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(interactive)) {
            return new WhatsAppException("Interactive message cannot be null.");
        }

        if (Objects.isNull(interactive.type())) {
            return new WhatsAppException("Message type cannot be null.");
        }

        if (interactive.type().equals(InteractiveType.BUTTON)) {
            if (Objects.isNull(interactive.action())) {
                return new WhatsAppException("Action cannot be null for button interactive messages.");
            }

            WhatsAppException buttonListException = validateButtonList(interactive.action().buttons());
            if (buttonListException != null) {
                return buttonListException;
            }

            for (var button : interactive.action().buttons()) {
                WhatsAppException buttonException = validateButton(button);
                if (buttonException != null) {
                    return buttonException;
                }
            }
        }

        return null;
    }

    /**
     * Validates the list of buttons in an interactive message.
     *
     * @param buttons The list of buttons.
     * @return A {@link WhatsAppException} if validation fails; otherwise, returns
     *         {@code null}.
     */
    private static WhatsAppException validateButtonList(List<InteractiveButton> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            return new WhatsAppException("Buttons list cannot be null or empty.");
        }

        int buttonCount = buttons.size();

        if (buttonCount < INTERACTIVE_MIN_BUTTONS) {
            return new WhatsAppException(
                    String.format("At least %d button(s) are required.", INTERACTIVE_MIN_BUTTONS));
        }

        if (buttonCount > INTERACTIVE_MAX_BUTTONS) {
            return new WhatsAppException(
                    String.format("A maximum of %d buttons are allowed.", INTERACTIVE_MAX_BUTTONS));
        }

        return null;
    }

    /**
     * Validates an individual button in an interactive message.
     *
     * @param button The button to validate.
     * @return A {@link WhatsAppException} if validation fails; otherwise, returns
     *         {@code null}.
     */
    private static WhatsAppException validateButton(InteractiveButton button) {
        if (button.reply() == null || button.reply().title() == null) {
            return new WhatsAppException("Button title cannot be null.");
        }

        int titleLength = button.reply().title().length();

        if (titleLength < 1) {
            return new WhatsAppException("Button title must contain at least 1 character.");
        }

        if (titleLength > 20) {
            return new WhatsAppException("Button title cannot exceed 20 characters.");
        }

        return null;
    }

}
