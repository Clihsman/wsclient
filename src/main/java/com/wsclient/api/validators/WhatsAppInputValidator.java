package com.wsclient.api.validators;

import static com.wsclient.api.constants.WhatsAppConstants.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.wsclient.api.messages.request.Location;
import com.wsclient.api.messages.request.Media;
import com.wsclient.api.messages.request.Reaction;
import com.wsclient.api.messages.request.Text;
import com.wsclient.api.messages.request.contact.Contact;
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

        return null;
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

    /**
     * Validates a list of interactive sections used in WhatsApp interactive message
     * templates.
     * <p>
     * This method performs a full validation of a list of
     * {@link InteractiveSection} objects to ensure
     * they comply with the structural and content constraints required by
     * WhatsApp's messaging API.
     * </p>
     *
     * <h3>Validation rules:</h3>
     * <ul>
     * <li>The list of sections must not be {@code null} or empty.</li>
     * <li>A maximum of 10 sections is allowed.</li>
     * <li>Section titles:
     * <ul>
     * <li>Must not be {@code null} or empty.</li>
     * <li>Cannot exceed 24 characters.</li>
     * </ul>
     * </li>
     * <li>Each section must contain between 1 and 10 rows.</li>
     * <li>Rows:
     * <ul>
     * <li>Each row must have a non-empty {@code id} (max 200 characters).</li>
     * <li>Each row must have a non-empty {@code title} (max 24 characters).</li>
     * <li>{@code description} is optional but cannot exceed 72 characters.</li>
     * </ul>
     * </li>
     * <li>No duplicate row IDs are allowed across sections.</li>
     * </ul>
     *
     * @param interactiveSections the list of {@link InteractiveSection} objects to
     *                            validate
     * @return an {@link IllegalArgumentException} describing the first validation
     *         error found,
     *         or {@code null} if the list is valid.
     *
     * @throws IllegalArgumentException if any rule above is violated
     *                                  (the method does not throw directly; it
     *                                  returns the exception instance instead).
     *
     * @see InteractiveSection
     * @see InteractiveSectionRow
     */
    private static IllegalArgumentException validateSectionList(List<InteractiveSection> interactiveSections) {

        if (interactiveSections == null || interactiveSections.size() < INTERACTIVE_MIN_SECTIONS) {
            return new IllegalArgumentException("List interactive must contain at least one section.");
        }

        if (interactiveSections.size() > INTERACTIVE_MAX_SECTIONS) {
            return new IllegalArgumentException(
                    String.format("You can include up to %d sections maximum.", INTERACTIVE_MAX_SECTIONS));
        }

        IllegalArgumentException duplicateError = validateDuplicateRowIds(interactiveSections);
        if (duplicateError != null)
            return duplicateError;

        // Validar cada sección individual
        for (InteractiveSection section : interactiveSections) {

            if (section.getTitle() == null || section.getTitle().trim().isEmpty()) {
                return new IllegalArgumentException("Each section must have a title.");
            }

            if (section.getTitle().length() > INTERACTIVE_MAX_SECTION_TITLE_LENGTH) {
                return new IllegalArgumentException(
                        String.format("Section title cannot exceed %d characters.",
                                INTERACTIVE_MAX_SECTION_TITLE_LENGTH));
            }

            List<InteractiveSectionRow> rows = section.getRows();

            if (rows == null || rows.size() < INTERACTIVE_MIN_LIST_ROWS) {
                return new IllegalArgumentException("Each section must contain at least one row.");
            }

            if (rows.size() > INTERACTIVE_MAX_LIST_ROWS) {
                return new IllegalArgumentException(
                        String.format("Each section can contain up to %d rows maximum.", INTERACTIVE_MAX_LIST_ROWS));
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

                if (row.getTitle().length() > INTERACTIVE_MAX_ROW_TITLE_LENGTH) {
                    return new IllegalArgumentException(
                            String.format("Row title cannot exceed %d characters.", INTERACTIVE_MAX_ROW_TITLE_LENGTH));
                }

                if (row.getDescription() != null
                        && row.getDescription().length() > INTERACTIVE_MAX_ROW_DESCRIPTION_LENGTH) {
                    return new IllegalArgumentException(
                            String.format("Row description cannot exceed %d characters.",
                                    INTERACTIVE_MAX_ROW_DESCRIPTION_LENGTH));
                }
            }
        }

        return null;
    }

    /**
     * Validates the input parameters for sending any WhatsApp media message.
     * <p>
     * This method performs generic validation applicable to all media types
     * (e.g., images, videos, documents). It ensures that the recipient number and
     * the provided {@link Media} object comply with WhatsApp API constraints.
     * </p>
     *
     * <p>
     * <strong>Validation Rules:</strong>
     * </p>
     * <ul>
     * <li>The recipient's phone number (<code>to</code>) cannot be null and must
     * contain only digits (e.g., "573001112233").</li>
     * <li>The {@link Media} object cannot be null.</li>
     * <li>The {@link Media} object must include either a valid <code>id</code>
     * (for previously uploaded media) or a publicly accessible
     * <code>link</code>.</li>
     * <li>If a <code>link</code> is provided, it must be a valid HTTP or HTTPS
     * URL.</li>
     * <li>If a <code>caption</code> is present, it must not exceed 1024
     * characters.</li>
     * </ul>
     *
     * @param to    The recipient's phone number in international format.
     * @param media The {@link Media} object containing the media metadata (ID,
     *              link,
     *              caption, etc.).
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateMediaInput(String to, Media media) {

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

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp image message.
     *
     * <p>
     * This method ensures that both the recipient and the image data
     * comply with WhatsApp API constraints. The recipient must be a valid
     * numeric string, and the {@link Media} object must include either a
     * valid {@code id} or a valid {@code link}. Captions are optional but
     * cannot exceed 1024 characters.
     * </p>
     *
     * @param to    The recipient's phone number in international format.
     *              It must contain only digits (e.g., "573001112233").
     * @param image The {@link Media} object containing the image information.
     *              It must include either a valid {@code id} or a valid
     *              {@code link}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateImageInput(String to, Media image) {

        IllegalArgumentException exceptionValidateMedia = validateMediaInput(to, image);

        if (exceptionValidateMedia != null)
            return exceptionValidateMedia;

        if (image.getFilename() != null && !image.getFilename().isBlank()) {
            return new IllegalArgumentException("Filename is not allowed for image messages.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp video message.
     *
     * <p>
     * This method ensures that both the recipient and the video data comply with
     * WhatsApp API constraints. The recipient must be a valid numeric string,
     * and the {@link Media} object must include either a valid {@code id} or a
     * valid {@code link}. Captions are optional but cannot exceed 1024 characters.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> The {@code filename} field is not allowed for
     * video messages, as the WhatsApp Cloud API ignores or rejects it.
     * </p>
     *
     * @param to    The recipient's phone number in international format.
     *              It must contain only digits (e.g., "573001112233").
     * @param video The {@link Media} object containing the video information.
     *              It must include either a valid {@code id} or a valid
     *              {@code link}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateVideoInput(String to, Media video) {

        IllegalArgumentException exceptionValidateMedia = validateMediaInput(to, video);

        if (exceptionValidateMedia != null)
            return exceptionValidateMedia;

        if (video.getFilename() != null && !video.getFilename().isBlank()) {
            return new IllegalArgumentException("Filename is not allowed for video messages.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp audio message.
     *
     * <p>
     * This method ensures that both the recipient and the audio data comply with
     * WhatsApp Cloud API constraints. The recipient must be a valid numeric string,
     * and the {@link Media} object must include either a valid {@code id} or a
     * valid {@code link}.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> The {@code filename} field is not allowed for
     * audio messages, as the WhatsApp Cloud API ignores or rejects it.
     * </p>
     *
     * @param to    The recipient's phone number in international format.
     *              It must contain only digits (e.g., "573001112233").
     * @param audio The {@link Media} object containing the audio information.
     *              It must include either a valid {@code id} or a valid
     *              {@code link}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateAudioInput(String to, Media audio) {

        IllegalArgumentException exceptionValidateMedia = validateMediaInput(to, audio);

        if (exceptionValidateMedia != null)
            return exceptionValidateMedia;

        if (audio.getFilename() != null && !audio.getFilename().isBlank()) {
            return new IllegalArgumentException("Filename is not allowed for audio messages.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp document message.
     *
     * @param to       The recipient's phone number in international format.
     *                 It must contain only digits (e.g., "573001112233").
     * @param document The {@link Media} object containing the document information.
     *                 It must include either a valid {@code id} or {@code link},
     *                 and
     *                 must always include a valid {@code filename}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateDocumentInput(String to, Media document) {
        IllegalArgumentException exceptionValidateMedia = validateMediaInput(to, document);

        if (exceptionValidateMedia != null)
            return exceptionValidateMedia;

        if (document.getFilename() == null || document.getFilename().isBlank()) {
            return new IllegalArgumentException("Filename is required for document messages.");
        }

        if (document.getFilename().length() > 240) {
            return new IllegalArgumentException("Filename exceeds maximum length of 240 characters.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp sticker message.
     *
     * <p>
     * Stickers follow the same {@code id}/{@code link} rules as other media
     * types, but unlike image/video/document they do not support a
     * {@code caption} or {@code filename}.
     * </p>
     *
     * @param to      The recipient's phone number in international format.
     *                It must contain only digits (e.g., "573001112233").
     * @param sticker The {@link Media} object containing the sticker information.
     *                It must include either a valid {@code id} or a valid
     *                {@code link}, and must not include a {@code caption} or
     *                {@code filename}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateStickerInput(String to, Media sticker) {

        IllegalArgumentException exceptionValidateMedia = validateMediaInput(to, sticker);

        if (exceptionValidateMedia != null)
            return exceptionValidateMedia;

        if (sticker.getFilename() != null && !sticker.getFilename().isBlank()) {
            return new IllegalArgumentException("Filename is not allowed for sticker messages.");
        }

        if (sticker.getCaption() != null && !sticker.getCaption().isBlank()) {
            return new IllegalArgumentException("Caption is not allowed for sticker messages.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp location message.
     *
     * @param to       The recipient's phone number in international format.
     *                 It must contain only digits (e.g., "573001112233").
     * @param location The {@link Location} object containing the latitude and
     *                 longitude to send.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateLocationInput(String to, Location location) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException("Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(location)) {
            return new IllegalArgumentException("Location object cannot be null.");
        }

        if (location.getLatitude() == null || location.getLatitude().isBlank()) {
            return new IllegalArgumentException("Location latitude cannot be null or empty.");
        }

        if (location.getLongitude() == null || location.getLongitude().isBlank()) {
            return new IllegalArgumentException("Location longitude cannot be null or empty.");
        }

        final double latitude;
        final double longitude;

        try {
            latitude = Double.parseDouble(location.getLatitude());
        } catch (NumberFormatException e) {
            return new IllegalArgumentException("Location latitude must be a valid number.");
        }

        try {
            longitude = Double.parseDouble(location.getLongitude());
        } catch (NumberFormatException e) {
            return new IllegalArgumentException("Location longitude must be a valid number.");
        }

        if (latitude < LOCATION_MIN_LATITUDE || latitude > LOCATION_MAX_LATITUDE) {
            return new IllegalArgumentException(
                    String.format("Location latitude must be between %s and %s.",
                            LOCATION_MIN_LATITUDE, LOCATION_MAX_LATITUDE));
        }

        if (longitude < LOCATION_MIN_LONGITUDE || longitude > LOCATION_MAX_LONGITUDE) {
            return new IllegalArgumentException(
                    String.format("Location longitude must be between %s and %s.",
                            LOCATION_MIN_LONGITUDE, LOCATION_MAX_LONGITUDE));
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp reaction message.
     *
     * @param to       The recipient's phone number in international format.
     *                 It must contain only digits (e.g., "573001112233").
     * @param reaction The {@link Reaction} object containing the target message
     *                 ID and emoji. An empty emoji ({@code ""}) is valid and
     *                 removes a previously sent reaction.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateReactionInput(String to, Reaction reaction) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException("Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (Objects.isNull(reaction)) {
            return new IllegalArgumentException("Reaction object cannot be null.");
        }

        if (reaction.getMessage_id() == null || reaction.getMessage_id().isBlank()) {
            return new IllegalArgumentException("Reaction message ID cannot be null or empty.");
        }

        if (reaction.getEmoji() == null) {
            return new IllegalArgumentException(
                    "Reaction emoji cannot be null. Use an empty string to remove a reaction.");
        }

        return null;
    }

    /**
     * Validates the input parameters for sending a WhatsApp contacts message.
     *
     * @param to       The recipient's phone number in international format.
     *                 It must contain only digits (e.g., "573001112233").
     * @param contacts The list of {@link Contact} objects to send. Must contain
     *                 at least one contact, and each contact must include a
     *                 {@code name} with a non-empty {@code formatted_name}.
     * @return An {@link IllegalArgumentException} if any validation fails;
     *         otherwise, returns {@code null}.
     */
    public static IllegalArgumentException validateContactsInput(String to, List<Contact> contacts) {

        if (Objects.isNull(to)) {
            return new IllegalArgumentException("Recipient number cannot be null.");
        }

        if (!to.matches("\\d+")) {
            return new IllegalArgumentException("Invalid recipient number. The 'to' field must contain only digits.");
        }

        if (contacts == null || contacts.isEmpty()) {
            return new IllegalArgumentException("Contacts message must contain at least one contact.");
        }

        for (Contact contact : contacts) {
            if (contact == null) {
                return new IllegalArgumentException("Contact cannot be null.");
            }

            if (contact.name() == null || contact.name().formattedName() == null
                    || contact.name().formattedName().isBlank()) {
                return new IllegalArgumentException("Each contact must have a name with a non-empty formatted_name.");
            }
        }

        return null;
    }

}
