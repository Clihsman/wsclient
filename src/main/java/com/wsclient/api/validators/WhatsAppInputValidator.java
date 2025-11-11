package com.wsclient.api.validators;

import static com.wsclient.api.constants.WhatsAppConstants.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.wsclient.api.messages.request.Media;
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
    public static IllegalArgumentException validateTextInput(String to, Text text) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (Objects.isNull(text)) {
            return new IllegalArgumentException("Text object cannot be null.");
        }

        if (Objects.isNull(text.getBody())) {
            return new IllegalArgumentException("Message body cannot be null.");
        }

        final String message = text.getBody().trim();

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

        if (Objects.isNull(interactive.getBody()) ||
                interactive.getBody().getText() == null ||
                interactive.getBody().getText().trim().isEmpty()) {
            return new IllegalArgumentException("Interactive body text is required and cannot be empty.");
        }

        if (Objects.isNull(interactive.getType())) {
            return new IllegalArgumentException("Message type cannot be null.");
        }

        if (interactive.getType().equals(InteractiveType.BUTTON)) {
            if (Objects.isNull(interactive.getAction())) {
                return new IllegalArgumentException("Action cannot be null for button interactive messages.");
            }

            final IllegalArgumentException exceptionValidateButtonList = validateButtonList(
                    interactive.getAction().getButtons());

            if (exceptionValidateButtonList != null) {
                return exceptionValidateButtonList;
            }
        }

        if (interactive.getType().equals(InteractiveType.LIST)) {

            if (Objects.isNull(interactive.getAction())) {
                return new IllegalArgumentException("Action cannot be null for list interactive messages.");
            }

            String buttonText = interactive.getAction().getButton();
            if (buttonText == null || buttonText.trim().isEmpty()) {
                return new IllegalArgumentException("List interactive must have a list button title.");
            }

            if (buttonText.length() > 20) {
                return new IllegalArgumentException("Button text cannot exceed 20 characters.");
            }

            final IllegalArgumentException exceptionValidateSectionList = validateSectionList(
                    interactive.getAction().getSections());
            if (exceptionValidateSectionList != null) {
                return exceptionValidateSectionList;
            }
        }

        return null;
    }

    /**
     * Validates that all row IDs within the provided list of interactive sections
     * are unique.
     *
     * <p>
     * This method ensures that each {@link InteractiveSectionRow} across all
     * sections
     * in an interactive list message has a distinct {@code id}. Duplicate row IDs
     * are not
     * allowed in WhatsApp interactive list templates, as each row must be uniquely
     * identifiable
     * when the user interacts with the message.
     * </p>
     *
     * <p>
     * If any duplicate IDs are found, this method returns an
     * {@link IllegalArgumentException}
     * describing the duplicated values. Otherwise, it returns {@code null},
     * indicating that
     * all row IDs are unique and valid.
     * </p>
     *
     * <p>
     * <strong>Example:</strong>
     * </p>
     * 
     * <pre>{@code
     * List<InteractiveSection> sections = List.of(
     *         new InteractiveSection("Main", List.of(
     *                 new InteractiveSectionRow("1", "Option A", null),
     *                 new InteractiveSectionRow("2", "Option B", null))),
     *         new InteractiveSection("Secondary", List.of(
     *                 new InteractiveSectionRow("3", "Option C", null),
     *                 new InteractiveSectionRow("1", "Option D", null) // <-- Duplicate ID
     *         )));
     *
     * IllegalArgumentException ex = validateDuplicateRowIds(sections);
     * if (ex != null)
     *     throw ex; // "Duplicate row IDs found: 1"
     * }</pre>
     *
     * @param interactiveSections
     *                            the list of {@link InteractiveSection} objects to
     *                            validate; each section may contain multiple rows
     *
     * @return an {@link IllegalArgumentException} if duplicate row IDs are
     *         detected,
     *         or {@code null} if all IDs are unique and valid
     *
     * @throws IllegalArgumentException
     *                                  if the input list is {@code null} or empty,
     *                                  since at least one section is required
     */
    private static IllegalArgumentException validateDuplicateRowIds(List<InteractiveSection> interactiveSections) {

        if (interactiveSections == null || interactiveSections.isEmpty()) {
            return new IllegalArgumentException("At least one section is required to check for duplicate IDs.");
        }

        List<String> allIds = interactiveSections.stream()
                .filter(Objects::nonNull)
                .flatMap(section -> section.getRows().stream())
                .filter(Objects::nonNull)
                .map(InteractiveSectionRow::getId)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toList());

        Set<String> uniqueIds = new HashSet<>();
        List<String> duplicates = allIds.stream()
                .filter(id -> !uniqueIds.add(id))
                .distinct()
                .collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            return new IllegalArgumentException(
                    String.format("Duplicate row IDs found: %s", String.join(", ", duplicates)));
        }

        return null; // ✅ Todo correcto
    }

    /**
     * Validates that no duplicate button IDs exist within the provided list of
     * interactive button replies.
     *
     * <p>
     * This method is typically used for {@code InteractiveFactory.createButton()}
     * messages.
     * Each button must have a unique {@code id}. If duplicates are detected, an
     * {@link IllegalArgumentException} is returned with details of the duplicated
     * IDs.
     * </p>
     *
     * @param interactiveButtonReplies list of button replies to validate
     * @return an {@link IllegalArgumentException} describing duplicates, or
     *         {@code null} if all IDs are unique
     */
    private static IllegalArgumentException validateDuplicateButtonIds(
            List<InteractiveButtonReply> interactiveButtonReplies) {

        if (interactiveButtonReplies == null || interactiveButtonReplies.isEmpty()) {
            return new IllegalArgumentException("At least one button is required to check for duplicate IDs.");
        }

        // Extract all button IDs
        List<String> allIds = interactiveButtonReplies.stream()
                .filter(Objects::nonNull)
                .map(InteractiveButtonReply::getId)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .collect(Collectors.toList());

        // Find duplicates
        Set<String> uniqueIds = new HashSet<>();
        List<String> duplicates = allIds.stream()
                .filter(id -> !uniqueIds.add(id))
                .distinct()
                .collect(Collectors.toList());

        if (!duplicates.isEmpty()) {
            return new IllegalArgumentException(
                    String.format("Duplicate button IDs found: %s", String.join(", ", duplicates)));
        }

        return null; // ✅ All IDs are unique
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
            return new IllegalArgumentException("Button interactive must contain at least one button.");
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

        for (InteractiveButton interactiveButton : buttons) {
            final IllegalArgumentException exceptionValidateButton = validateButton(interactiveButton);
            if (exceptionValidateButton != null)
                return exceptionValidateButton;
        }

        return validateDuplicateButtonIds(
                buttons.stream().map(e -> e.getReply()).toList());
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

        InteractiveButtonReply buttonReply = Optional.ofNullable(button.getReply())
                .orElseThrow(() -> new IllegalArgumentException("Button reply cannot be null."));

        int titleLength = Optional.ofNullable(buttonReply.getTitle()).map(String::length)
                .orElseThrow(() -> new IllegalArgumentException("Button title cannot be null."));

        if (titleLength < 1) {
            return new IllegalArgumentException("Button title must contain at least 1 character.");
        }

        if (titleLength > 20) {
            return new IllegalArgumentException("Button title cannot exceed 20 characters.");
        }

        return null;
    }

    private static IllegalArgumentException validateSectionList(List<InteractiveSection> interactiveSections) {

        if (interactiveSections == null || interactiveSections.isEmpty()) {
            return new IllegalArgumentException("List interactive must contain at least one section.");
        }

        if (interactiveSections.size() > 10) {
            return new IllegalArgumentException("You can include up to 10 sections maximum.");
        }

        IllegalArgumentException duplicateError = validateDuplicateRowIds(interactiveSections);
        if (duplicateError != null)
            return duplicateError;

        // Validar cada sección individual
        for (InteractiveSection section : interactiveSections) {

            if (section.getTitle() == null || section.getTitle().trim().isEmpty()) {
                return new IllegalArgumentException("Each section must have a title.");
            }

            if (section.getTitle().length() > 24) {
                return new IllegalArgumentException("Section title cannot exceed 24 characters.");
            }

            List<InteractiveSectionRow> rows = section.getRows();

            if (rows == null || rows.isEmpty()) {
                return new IllegalArgumentException("Each section must contain at least one row.");
            }

            if (rows.size() > 10) {
                return new IllegalArgumentException("Each section can contain up to 10 rows maximum.");
            }

            for (InteractiveSectionRow row : rows) {
                if (row.getId() == null || row.getId().trim().isEmpty()) {
                    return new IllegalArgumentException("Each row must have a non-empty ID.");
                }

                if (row.getId().length() > 200) {
                    return new IllegalArgumentException("Row ID cannot exceed 200 characters.");
                }

                if (row.getTitle() == null || row.getTitle().trim().isEmpty()) {
                    return new IllegalArgumentException("Each row must have a title.");
                }

                if (row.getTitle().length() > 24) {
                    return new IllegalArgumentException("Row title cannot exceed 24 characters.");
                }

                if (row.getDescription() != null && row.getDescription().length() > 72) {
                    return new IllegalArgumentException("Row description cannot exceed 72 characters.");
                }
            }
        }

        return null; // ✅ Si todo es válido
    }

    /**
     * /**
     * Validates the input parameters for sending a WhatsApp image message.
     *
     * @param to    The recipient's phone number in international format.
     *              It must contain only digits (e.g., "573001112233").
     * @param media The {@link Media} object containing the image information.
     *              It must include either a valid {@code id} or a valid
     *              {@code link}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateImageInput(String to, Media media) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException("Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(media)) {
            return new IllegalArgumentException("Media object cannot be null.");
        }

        if ((media.getId() == null || media.getId().isBlank()) &&
                (media.getLink() == null || media.getLink().isBlank())) {
            return new IllegalArgumentException("Media must have either an 'id' or a 'link' defined.");
        }

        if (media.getLink() != null && !media.getLink().isBlank() &&
                !media.getLink().matches("^https?://.+")) {
            return new IllegalArgumentException("Invalid media link. Only HTTP/HTTPS URLs are allowed.");
        }

        if (media.getCaption() != null && media.getCaption().length() > 1024) {
            return new IllegalArgumentException("Caption exceeds maximum length of 1024 characters.");
        }

        if (media.getFilename() != null && media.getFilename().length() > 255) {
            return new IllegalArgumentException("Filename exceeds maximum length of 255 characters.");
        }

        return null;
    }
}
