package com.wsclient.api.messages.request.interactive;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;

/**
 * The <code>Interactive Object</code> contains four main components:
 * <code>header</code>,
 * <code>body</code>, <code>footer</code>,
 * and <code>action</code>. Additionally, some of those components can contain
 * one or more
 * different objects:
 * 
 * <ul>
 * <li>
 * Inside <code>header</code>, you can nest media objects.
 * </li>
 * <li>
 * Inside <code>action</code>, you can nest section and button objects.
 * </li>
 * </ul>
 * 
 * @param type   type
 * @param header header
 * @param body   body
 * @param footer footer
 * @param action action
 */
@Builder
public record Interactive(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The type of interactive message you want to send. Supported values:
         * list: Use it for List Messages.
         * </p>
         * 
         * <ul>
         * <li>
         * <code>button</code>: Use it for Reply Buttons.
         * </li>
         * <li>
         * <code>product</code>: Use this for Single Product Messages
         * </li>
         * <li>
         * <code>product_list</code>: Use this for Multi-Product Messages
         * </li>
         * </ul>
         */
        InteractiveType type,
        /**
         * <strong>
         * Required for type <code>product_list</code>. Optional for other types.
         * </strong>
         * 
         * <p>
         * This contains the header content displayed on top of a message. You cannot
         * set a header if your <code>interactive</code> object is type
         * <code>product</code>.
         * </p>
         * 
         * <p>
         * The header object contains the following fields:
         * </p>
         * 
         * <li>
         * <code>document</code>: Required if type is set to document. Contains the
         * media object with
         * the document.
         * 
         * </li>
         * <li>
         * <code>image</code>: Required if type is set to image. Contains the media
         * object with the
         * image.
         * </li>
         * <li>
         * <code>video</code>: Required if type is set to video. Contains the media
         * object with the
         * video.
         * </li>
         * <li>
         * <code>text</code>: Required if type is set to text. The text for the header.
         * Formatting
         * allows emojis, but not markdown. Maximum length: 60 characters.
         * </li>
         *
         * <strong>
         * Supported interactive message type by header type:
         * </strong>
         * 
         * <li>
         * <code>text</code> - for List Messages, Reply Buttons, and Multi-Product
         * Messages
         * </li>
         * <li>
         * <code>video</code> - for Reply Buttons.
         * </li>
         * <li>
         * image: for Reply Buttons.
         * </li>
         * <li>
         * <code>document</code> - For Reply Buttons.
         * </li>
         * 
         */
        InteractiveHeader header,
        /**
         * <strong>
         * Optional for type product.
         * </strong>
         * 
         * <p>
         * <strong>Required</strong> for all other message types.
         * </p>
         * 
         * <p>
         * The body of the message. The text field for the body object supports Emojis
         * <p>
         * and markdown.
         * 
         * Maximum length: 1024 characters.
         */
        InteractiveBody body,
        /**
         * <strong>
         * Optional.
         * </strong>
         * 
         * <p>
         * An object with the footer of the message.Emojis and markdown are supported.
         * </p>
         * 
         * Maximum length: 60 characters.
         */
        String footer,
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The action you want the user to perform after reading the message.
         * </p>
         */
        InteractiveAction action) {
    /**
     * InteractiveType
     */
    public enum InteractiveType {
        /**
         * LIST
         */
        LIST("list"),
        /**
         * BUTTON
         */
        BUTTON("button"),
        /**
         * PRODUCT
         */
        PRODUCT("product"),
        /**
         * PRODUCT_LIST
         */
        PRODUCT_LIST("product");

        private final String value;

        InteractiveType(String value) {
            this.value = value;
        }

        /**
         * getValue
         * 
         * @return value
         */
        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
