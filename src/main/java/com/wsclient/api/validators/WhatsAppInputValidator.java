package com.wsclient.api.validators;

import static com.wsclient.api.constants.WhatsAppConstants.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.request.interactive.InteractiveButton;
import com.wsclient.api.messages.request.interactive.InteractiveButtonReply;
import com.wsclient.api.messages.request.interactive.InteractiveSection;
import com.wsclient.api.messages.request.interactive.InteractiveSectionRow;
import com.wsclient.api.messages.request.interactive.Interactive.InteractiveType;

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
     * WhatsAppInputValidator
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
    public static IllegalArgumentException validateMessageInput(String to, Text text) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (Objects.isNull(text)) {
            return new IllegalArgumentException("Text object cannot be null.");
        }

        if (Objects.isNull(text.body())) {
            return new IllegalArgumentException("Message body cannot be null.");
        }

        final String message = text.body().trim();

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (message.length() < MESSAGE_MIN_TEXT) {
            return new IllegalArgumentException(
                    String.format("Message is too short. Minimum length allowed is %d characters.",
                            MESSAGE_MIN_TEXT));
        }

        if (message.length() > MESSAGE_MAX_TEXT) {
            return new IllegalArgumentException(
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
     * @return A {@link IllegalArgumentException} if any validation fails;
     *         otherwise,
     *         returns {@code null}.
     */
    public static IllegalArgumentException validateInteractiveInput(String to, Interactive interactive) {
        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException(
                    "Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(interactive)) {
            return new IllegalArgumentException("Interactive message cannot be null.");
        }

        if (Objects.isNull(interactive.type())) {
            return new IllegalArgumentException("Message type cannot be null.");
        }

        if (interactive.type().equals(InteractiveType.BUTTON)) {
            if (Objects.isNull(interactive.action())) {
                return new IllegalArgumentException("Action cannot be null for button interactive messages.");
            }

            validateButtonList(interactive.action().buttons());

            for (var button : interactive.action().buttons()) {
                validateButton(button);
            }
        }

        if (interactive.type().equals(InteractiveType.LIST)) {
            if (Objects.isNull(interactive.action())) {
                throw new IllegalArgumentException("Action cannot be null for list interactive messages.");
            }

            final IllegalArgumentException exceptionValidateButtonList = validateSectionList(
                    interactive.action().sections());
            if (exceptionValidateButtonList != null) {
                return exceptionValidateButtonList;
            }
            /*
             * for (var button : interactive.action().buttons()) {
             * final IllegalArgumentException exceptionValidateButton =
             * validateButton(button);
             * if (exceptionValidateButton != null) {
             * return exceptionValidateButton;
             * }
             * }
             */
        }

        return null;
    }

    /**
     * Validates the list of buttons in an interactive message.
     *
     * @param buttons The list of buttons.
     * @return A {@link IllegalArgumentException} if validation fails; otherwise,
     *         returns
     *         {@code null}.
     */
    private static IllegalArgumentException validateButtonList(List<InteractiveButton> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            throw new IllegalArgumentException("Buttons list cannot be null or empty.");
        }

        int buttonCount = buttons.size();

        if (buttonCount < INTERACTIVE_MIN_BUTTONS) {
            return new IllegalArgumentException(
                    String.format("At least %d button(s) are required.", INTERACTIVE_MIN_BUTTONS));
        }

        if (buttonCount > INTERACTIVE_MAX_BUTTONS) {
            return new IllegalArgumentException(
                    String.format("A maximum of %d buttons are allowed.", INTERACTIVE_MAX_BUTTONS));
        }

        return null;
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

        InteractiveButtonReply buttonReply = Optional.ofNullable(button.reply())
                .orElseThrow(() -> new IllegalArgumentException("Button reply cannot be null."));

        int titleLength = Optional.ofNullable(buttonReply.title()).map(String::length)
                .orElseThrow(() -> new IllegalArgumentException("Button title cannot be null."));

        if (titleLength < 1) {
            throw new IllegalArgumentException("Button title must contain at least 1 character.");
        }

        if (titleLength > 20) {
            throw new IllegalArgumentException("Button title cannot exceed 20 characters.");
        }

        return null;
    }

    private static IllegalArgumentException validateSectionList(List<InteractiveSection> interactiveSections) {

        List<InteractiveSectionRow> interactiveSectionRows = interactiveSections.stream()
                .map(InteractiveSection::rows).flatMap(List::stream).collect(Collectors.toList());

        if (interactiveSectionRows.size() > 10) {
            throw new IllegalArgumentException("asdasd");
        }

        return null;
    }
}
