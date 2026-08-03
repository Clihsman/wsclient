package com.wsclient.api.messages.request.interactive;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * InteractiveAction
 * 
 */
@Builder
@Data
public class InteractiveAction {

    /***
     * Action name
     */
    private String name;

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

    /**
     * <strong>
     * Required for {@code cta_url} messages.
     * </strong>
     *
     * <p>
     * A map of the CTA URL button's parameters. For {@code cta_url}, WhatsApp
     * expects the keys {@code display_text} and {@code url}, which is exactly
     * how this map serializes.
     * </p>
     */
    private Map<String, String> parameters;

    /**
     * <strong>
     * Required for {@code product} and {@code product_list} messages.
     * </strong>
     *
     * <p>
     * The ID of the catalog connected to the WhatsApp Business Account that
     * the referenced product(s) belong to.
     * </p>
     */
    @JsonProperty("catalog_id")
    private String catalogId;

    /**
     * <strong>
     * Required for {@code product} messages (Single Product Messages).
     * </strong>
     *
     * <p>
     * Unique identifier of the product in the catalog referenced by
     * {@link #catalogId}.
     * </p>
     */
    @JsonProperty("product_retailer_id")
    private String productRetailerId;
}
