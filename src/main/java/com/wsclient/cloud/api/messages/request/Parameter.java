package com.wsclient.cloud.api.messages.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;

/**
 * The Parameter Object contains the following fields
 */
@Builder
public record Parameter(
        /**
         * <strong>Required.</strong>
         * <p>
         * Describes the parameter type.
         * </p>
         * <p>
         * <strong>Values:</strong> <code>text</code>, <code>currency</code>,
         * <code>date_time</code>, <code>image</code>, <code>document</code>
         * </p>
         * For text-based templates, the only supported parameter types are
         * <code>text</code>,
         * <code>currency</code>, and <code>date_time</code>
         */
        ParameterType type,
        /**
         * <strong>
         * Required when type=text.
         * </strong>
         * <p>
         * The message of the text.
         * For the <code>header component</code>, the character limit is 60 characters.
         * For the <code>body component</code>, the character limit is 1024.
         * </p>
         * <p>
         * The exception to these character limits applies to template messages in the
         * following conditions:
         * </p>
         * <br>
         * <ul>
         * <li>
         * When sending a <code>template</code> message with a
         * <code>body component</code> only, the
         * character
         * limit for the text parameter and the full template text is 32768 characters.
         * </li>
         * 
         * <li>
         * When sending a <code>template</code> message with <code>body</code> and other
         * components, The character
         * limit for the <code>text</code> parameter and the full template text is 1024
         * characters.
         * </li>
         * </ul>
         * 
         */
        String text,
        /**
         * <strong>
         * Required when type is currency.
         * </strong>
         * <p>
         * A currency object.
         * </p>
         */
        Currency currency,
        /**
         * <strong>
         * Required when type is date_time.
         * </strong>
         * <p>
         * A <code>date_time</code> object.
         * </p>
         */
        @JsonProperty("date_time") DateTime dateTime,
        /**
         * <strong>
         * Required when type is <code>image</code>.
         * </strong>
         * <p>
         * A media object of type image.
         * </p>
         */
        Media image,
        /**
         * <strong>
         * Required when type is <code>document</code>.
         * </strong>
         * <p>
         * A media object of type document.
         * </p>
         * Only PDF documents are supported for media-based message templates.
         */
        Media document) {
    public enum ParameterType {
        TEXT("text"),
        CURRENCY("currency"),
        DATETIME("date_time"),
        IMAGE("image"),
        DOCUMENT("document");

        private final String value;

        ParameterType(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
