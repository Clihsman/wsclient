package com.wsclient.api.messages.factory;

import java.util.ArrayList;
import java.util.List;

import com.wsclient.api.messages.request.interactive.Interactive;
import com.wsclient.api.messages.request.interactive.InteractiveAction;
import com.wsclient.api.messages.request.interactive.InteractiveBody;
import com.wsclient.api.messages.request.interactive.InteractiveButton;
import com.wsclient.api.messages.request.interactive.InteractiveButtonReply;
import com.wsclient.api.messages.request.interactive.InteractiveSection;
import com.wsclient.api.messages.request.interactive.InteractiveSectionRow;
import com.wsclient.api.messages.request.interactive.Interactive.InteractiveType;

/**
 * Factory class for building {@link Interactive} WhatsApp message objects
 * in a clean and fluent way.
 * <p>
 * This class supports two types of interactive messages:
 * <ul>
 * <li>{@link InteractiveType#BUTTON} - For button-based messages.</li>
 * <li>{@link InteractiveType#LIST} - For list-based messages.</li>
 * </ul>
 * <p>
 * Usage example:
 * 
 * <pre>
 * // Example: Create a list-type interactive message
 * Interactive listMessage = InteractiveFactory.createList()
 *         .text("Choose an option:")
 *         .listButton("View options")
 *         .section("Main Menu")
 *         .row("1", "Option A", "Description A")
 *         .row("2", "Option B", "Description B")
 *         .build();
 *
 * // Example: Create a button-type interactive message
 * Interactive buttonMessage = InteractiveFactory.createButton()
 *         .text("Do you confirm your appointment?")
 *         .button("confirm", "Yes")
 *         .button("cancel", "No")
 *         .build();
 * </pre>
 */
public class InteractiveFactory {

    private InteractiveType type;
    private String text;
    private String button;
    private final List<InteractiveSection> sections = new ArrayList<>();
    private final List<InteractiveButton> buttons = new ArrayList<>();

    /**
     * Sets the type of the interactive message.
     *
     * @param type the interactive message type (BUTTON or LIST)
     * @return the current factory instance for method chaining
     */
    public InteractiveFactory type(InteractiveType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds a new section to the interactive message (used only for LIST type).
     *
     * @param title the title of the section
     * @return the current factory instance
     */
    public InteractiveFactory section(String title) {
        sections.add(InteractiveSection.builder().title(title).rows(new ArrayList<>()).build());
        return this;
    }

    /**
     * Adds a new row (option) to the most recently created section.
     * If no section exists, a default one is automatically created.
     *
     * @param id    the unique identifier of the row
     * @param title the title of the row
     * @return the current factory instance
     */
    public InteractiveFactory row(String id, String title) {
        return row(id, title, null);
    }

    /**
     * Adds a new row (option) to the most recently created section,
     * including an optional description.
     * If no section exists, a default one is automatically created.
     *
     * @param id          the unique identifier of the row
     * @param title       the title of the row
     * @param description the optional description of the row
     * @return the current factory instance
     */
    public InteractiveFactory row(String id, String title, String description) {
        if (sections.isEmpty()) {
            section("default");
        }

        InteractiveSection section = sections.get(sections.size() - 1);
        List<InteractiveSectionRow> rows = section.getRows();
        if (rows == null) {
            rows = new ArrayList<>();
        }

        rows.add(InteractiveSectionRow.builder()
                .id(id)
                .title(title)
                .description(description)
                .build());
        section.setRows(rows);
        return this;
    }

    /**
     * Adds a new button to the interactive message (used only for BUTTON type).
     *
     * @param id    the unique identifier of the button
     * @param title the visible text of the button
     * @return the current factory instance
     */
    public InteractiveFactory button(String id, String title) {
        buttons.add(InteractiveButton.builder()
                .reply(InteractiveButtonReply.builder()
                        .id(id)
                        .title(title)
                        .build())
                .build());
        return this;
    }

    /**
     * Sets the button text used to open the list (for LIST type messages).
     * WhatsApp requires this field for list interactions.
     *
     * @param title the button label (max 20 characters)
     * @return the current factory instance
     */
    public InteractiveFactory listButton(String title) {
        this.button = title;
        return this;
    }

    /**
     * Sets the text content of the interactive message body.
     *
     * @param text the message body text
     * @return the current factory instance
     */
    public InteractiveFactory text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Builds and returns a fully configured {@link Interactive} object.
     * <p>
     * This method does not validate the resulting object. Field-level validation
     * (required type, minimum buttons/sections, length limits, duplicate IDs,
     * etc.) is performed by {@link com.wsclient.api.validators.WhatsAppInputValidator}
     * when the message is sent via {@link com.wsclient.api.services.WhatsAppClient}.
     *
     * @return the constructed {@link Interactive} message
     */
    public Interactive build() {
        return Interactive.builder()
                .type(type)
                .body(InteractiveBody.builder()
                        .text(text)
                        .build())
                .action(InteractiveAction.builder()
                        .button(button)
                        .sections(sections)
                        .buttons(buttons)
                        .build())
                .build();
    }

    /**
     * Creates a new instance of {@link InteractiveFactory}.
     *
     * @return a new factory instance
     */
    public static InteractiveFactory create() {
        return new InteractiveFactory();
    }

    /**
     * Creates a factory pre-configured for building
     * {@link InteractiveType#BUTTON} messages.
     *
     * @return a new button-type factory instance
     */
    public static InteractiveFactory createButton() {
        return create().type(InteractiveType.BUTTON);
    }

    /**
     * Creates a factory pre-configured for building
     * {@link InteractiveType#LIST} messages.
     *
     * @return a new list-type factory instance
     */
    public static InteractiveFactory createList() {
        return create().type(InteractiveType.LIST);
    }
}