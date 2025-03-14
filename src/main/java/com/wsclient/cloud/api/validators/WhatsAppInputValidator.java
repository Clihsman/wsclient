package com.wsclient.cloud.api.validators;

import java.util.List;
import java.util.Objects;

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
 * All validation methods return a {@link IllegalArgumentException} if the input
 * is
 * invalid;
 * otherwise, they return {@code null}.
 * </p>
 *
 * <h2>Example Usage:</h2>
 * 
 * <pre>
 * {@code
 * IllegalArgumentException exception = WhatsAppClientValidator.validateMessageInput(to, text);
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
     * 
     */
    private WhatsAppInputValidator() {

    }

    /**
     * Validates the input parameters for sending a WhatsApp message.
     *
     * @param to   The recipient's phone number as a string. It must contain only
     *             digits.
     * @param text The text object containing the message body.
     * @return A {@link IllegalArgumentException} if any validation fails;
     *         otherwise,
     *         returns {@code null}.
     */
    public static void validateMessageInput(String to, Text text) {

        if (Objects.isNull(to)) {
            throw new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (Objects.isNull(text)) {
            throw new IllegalArgumentException("Text object cannot be null.");
        }

        if (Objects.isNull(text.body())) {
            throw new IllegalArgumentException("Message body cannot be null.");
        }

        final String message = text.body().trim();

        if (!to.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (message.length() < MESSAGE_MIN_TEXT) {
            throw new IllegalArgumentException(
                    String.format("Message is too short. Minimum length allowed is %d characters.",
                            MESSAGE_MIN_TEXT));
        }

        if (message.length() > MESSAGE_MAX_TEXT) {
            throw new IllegalArgumentException(
                    String.format("Message exceeds max length of %d characters.",
                            MESSAGE_MAX_TEXT));
        }
    }

    /**
     * Validates the input parameters for sending an interactive WhatsApp message.
     *
     * @param to          The recipient's phone number as a string. It must contain
     *                    only digits.
     * @param interactive The interactive message object containing the message type
     *                    and actions.
     * @return A {@link IllegalArgumentException} if any validation fails;
     *         otherwise,
     *         returns {@code null}.
     */
    public static void validateInteractiveInput(String to, Interactive interactive) {
        if (Objects.isNull(to)) {
            throw new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            throw new IllegalArgumentException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(interactive)) {
            throw new IllegalArgumentException("Interactive message cannot be null.");
        }

        if (Objects.isNull(interactive.type())) {
            throw new IllegalArgumentException("Message type cannot be null.");
        }

        if (interactive.type().equals(InteractiveType.BUTTON)) {
            if (Objects.isNull(interactive.action())) {
                throw new IllegalArgumentException("Action cannot be null for button interactive messages.");
            }

            validateButtonList(interactive.action().buttons());

            for (var button : interactive.action().buttons()) {
                validateButton(button);
            }
        }

        if (interactive.type().equals(InteractiveType.LIST)) {
            if (Objects.isNull(interactive.action())) {
                throw new IllegalArgumentException("Action cannot be null for button interactive messages.");
            }

            validateButtonList(interactive.action().buttons());

            for (var button : interactive.action().buttons()) {
                validateButton(button);
            }
        }
    }

    /**
     * Validates the list of buttons in an interactive message.
     *
     * @param buttons The list of buttons.
     * @return A {@link IllegalArgumentException} if validation fails; otherwise,
     *         returns
     *         {@code null}.
     */
    private static void validateButtonList(List<InteractiveButton> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            throw new IllegalArgumentException("Buttons list cannot be null or empty.");
        }

        int buttonCount = buttons.size();

        if (buttonCount < INTERACTIVE_MIN_BUTTONS) {
            throw new IllegalArgumentException(
                    String.format("At least %d button(s) are required.", INTERACTIVE_MIN_BUTTONS));
        }

        if (buttonCount > INTERACTIVE_MAX_BUTTONS) {
            throw new IllegalArgumentException(
                    String.format("A maximum of %d buttons are allowed.", INTERACTIVE_MAX_BUTTONS));
        }
    }

    /**
     * Validates an individual button in an interactive message.
     *
     * @param button The button to validate.
     * @return A {@link IllegalArgumentException} if validation fails; otherwise,
     *         returns
     *         {@code null}.
     */
    private static IllegalArgumentException validateButton(InteractiveButton button) {
        if (button.reply() == null || button.reply().title() == null) {
            throw new IllegalArgumentException("Button title cannot be null.");
        }

        int titleLength = button.reply().title().length();

        if (titleLength < 1) {
            throw new IllegalArgumentException("Button title must contain at least 1 character.");
        }

        if (titleLength > 20) {
            throw new IllegalArgumentException("Button title cannot exceed 20 characters.");
        }

        return null;
    }

}
