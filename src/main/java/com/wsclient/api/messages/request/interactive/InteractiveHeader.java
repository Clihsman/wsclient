package com.wsclient.cloud.api.messages.request.interactive;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * InteractiveHeader
 * 
 * @param type     type
 * @param text     text
 * @param video    video
 * @param image    image
 * @param document document
 */
public record InteractiveHeader(
        /**
         * <strong>
         * Required.
         * </strong>
         * 
         * <p>
         * The header type you would like to use. Supported values are:
         * </p>
         * <li>
         * - <code>text</code>: Used for List Messages, Reply Buttons, and Multi-Product
         * Messages.
         * </li>
         * <li>
         * - <code>video</code>: Used for Reply Buttons.
         * </li>
         * <li>
         * - <code>image</code>: Used for Reply Buttons.
         * </li>
         * <li>
         * - <code>document</code>: Used for Reply Buttons.
         * </li>
         */
        InteractiveHeaderType type,
        /**
         * <strong>
         * Required if <code>type</code> is set to <code>text</code>.
         * </strong>
         * 
         * <p>
         * The text for the header. Formatting allows emojis, but not markdown.
         * </p>
         * 
         * Maximum length: 60 characters.
         */
        String text,
        /**
         * <strong>
         * Required if type is set to <code>video</code>.
         * </strong>
         * 
         * <p>
         * Contains the <code>media</code> object for this video.
         * </p>
         */
        InteractiveMedia video,
        /**
         * <strong>
         * Required if <code>type</code> is set to <code>image</code>.
         * </strong>
         * 
         * <p>
         * Contains the <code>media</code> object for this image.
         * </p>
         */
        InteractiveMedia image,
        /**
         * <strong>
         * Required if <code>type</code> is set to <code>document</code>.
         * </strong>
         * 
         * <p>
         * Contains the <code>media</code> object for this document.
         * </p>
         */
        InteractiveMedia document) {
    /**
     * InteractiveHeaderType
     */
    public enum InteractiveHeaderType {
        /**
         * TEXT
         */
        TEXT("text"),
        /**
         * VIDEO
         */
        VIDEO("video"),
        /**
         * IMAGE
         */
        IMAGE("image"),
        /**
         * DOCUMENT
         */
        DOCUMENT("document");

        private final String value;

        InteractiveHeaderType(String value) {
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
